package kz.university.ecosystem;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    // Мақала жазу бетін ашу
    @GetMapping("/articles/new")
    public String newArticleForm(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"TEACHER".equals(role)) {
            return "redirect:/home"; // Студенттер бұл бетке кіре алмайды
        }
        return "new-article";
    }

    // Мақаланы базаға сақтау
    @PostMapping("/articles/save")
    public String saveArticle(@RequestParam String title, 
                              @RequestParam String content) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCreatedAt(LocalDateTime.now());

        articleRepository.save(article); // ОСЫ ЖЕРДЕ БАЗАҒА САҚТАЛАДЫ
        return "redirect:/home";
    }
}