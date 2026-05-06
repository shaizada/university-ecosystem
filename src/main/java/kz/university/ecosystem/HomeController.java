package kz.university.ecosystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MessageRepository messageRepository;

    // 1. ТІРКЕЛУ
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, HttpSession session) {
        User savedUser = userRepository.save(user); 
        session.setAttribute("userId", savedUser.getId());
        session.setAttribute("userRole", savedUser.getRole());
        session.setAttribute("userName", savedUser.getFullName());
        return "redirect:/home";
    }

    // 2. КІРУ (LOGIN)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, 
                            @RequestParam String password, 
                            HttpSession session, 
                            Model model) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("userName", user.getFullName());
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Email немесе құпия сөз қате!");
            return "login";
        }
    }

    // 3. БАСТЫ БЕТ
    @GetMapping("/home")
    public String homePage(@RequestParam(name = "type", required = false) String type, 
                           HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<Article> articles = (type != null && !type.isEmpty()) 
            ? articleRepository.findByTypeOrderByCreatedAtDesc(type)
            : articleRepository.findAllByOrderByCreatedAtDesc();

        model.addAttribute("articles", articles);
        model.addAttribute("currentFilter", type);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        return "home"; 
    }

    // ЖАҢА: ЖАЗБА ҚОСУ (МАҚАЛА, ЖАҢАЛЫҚ, САУАЛНАМА)
    @PostMapping("/addArticle")
    public String addPost(@RequestParam String title, 
                          @RequestParam String content, 
                          @RequestParam String type, 
                          HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User author = userRepository.findById(userId).orElse(null);
        if (author != null) {
            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            article.setType(type); // ARTICLE, NEWS немесе POLL
            article.setAuthor(author);
            articleRepository.save(article);
        }
        return "redirect:/home";
    }

    // 4. ЖАҢАРТЫЛҒАН ЧАТ ЖҮЙЕСІ
    @GetMapping("/chats")
    public String chats(@RequestParam(name = "with", required = false) Long withUserId,
                        @RequestParam(name = "search", required = false) String search,
                        HttpSession session, Model model) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) return "redirect:/login";

        User currentUser = userRepository.findById(currentUserId).orElse(null);
        List<Message> lastMessages = new ArrayList<>();
        
        if (search != null && !search.trim().isEmpty()) {
            List<User> foundUsers = userRepository.findAll().stream()
                .filter(u -> u.getFullName().toLowerCase().contains(search.toLowerCase()) && !u.getId().equals(currentUserId))
                .collect(Collectors.toList());
            
            for (User u : foundUsers) {
                Message dummyMsg = new Message();
                dummyMsg.setSender(u);
                dummyMsg.setReceiver(currentUser);
                dummyMsg.setContent("");
                lastMessages.add(dummyMsg);
            }
            model.addAttribute("isSearch", true);
        } else {
            lastMessages = messageRepository.findLastMessagesByUser(currentUserId);
            model.addAttribute("isSearch", false);
        }

        model.addAttribute("lastMessages", lastMessages);
        model.addAttribute("searchQuery", search);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));

        if (withUserId != null) {
            User receiver = userRepository.findById(withUserId).orElse(null);
            if (receiver != null) {
                List<Message> chatHistory = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        currentUser, receiver, currentUser, receiver
                );
                model.addAttribute("chatWith", receiver);
                model.addAttribute("messages", chatHistory);
            }
        }

        String role = (String) session.getAttribute("userRole");
        return "TEACHER".equals(role) ? "teacher-chats" : "student-chats";
    }

    // ХАБАРЛАМА ЖІБЕРУ
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam Long receiverId, 
                              @RequestParam String content, 
                              HttpSession session) {
        Long senderId = (Long) session.getAttribute("userId");
        if (senderId == null) return "redirect:/login";

        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (sender != null && receiver != null && !content.trim().isEmpty()) {
            Message msg = new Message();
            msg.setSender(sender);
            msg.setReceiver(receiver);
            msg.setContent(content);
            messageRepository.save(msg);
        }

        return "redirect:/chats?with=" + receiverId;
    }

    // 5. ПРОФИЛЬ БЕТІ
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";

        long articlesCount = articleRepository.count(); // Базадағы мақала санын алады
        long chatsCount = messageRepository.count();   // Базадағы хабарлама санын алады
        
        model.addAttribute("articlesCount", articlesCount);
        model.addAttribute("pollsCount", 5); // Сауалнама санын әзірге қолмен жаза салсақ болады
        model.addAttribute("chatsCount", chatsCount);
        // ------------------------

        model.addAttribute("articles", articleRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        
        String role = (String) session.getAttribute("userRole");
        return "TEACHER".equals(role) ? "teacher-profile" : "student-profile";
    }

    // 6. БАПТАУЛАР БЕТІ
    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        
        return "settings";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }
    @PostMapping("/profile/upload-photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile file, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null || file.isEmpty()) return "redirect:/profile";

    try {
        // 1. Папка құру (static ішіндегі uploads)
        String uploadDir = "src/main/resources/static/uploads/";
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        // 2. Файл атын бірегей қылу
        String fileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        java.nio.file.Path filePath = uploadPath.resolve(fileName);
        java.nio.file.Files.write(filePath, file.getBytes());

        // 3. Базадағы қолданушының суретін жаңарту
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPhoto(fileName);
            userRepository.save(user);
            session.setAttribute("userPhoto", fileName); // Сессияны жаңарту
        }
    } catch (java.io.IOException e) {
        e.printStackTrace();
    }
    return "redirect:/profile";
}
}