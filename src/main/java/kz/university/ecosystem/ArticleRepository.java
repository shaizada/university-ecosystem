package kz.university.ecosystem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Уақыты бойынша сұрыптау
    List<Article> findAllByOrderByCreatedAtDesc();
    
    // Түрі бойынша фильтрлеу және сұрыптау (ФИЛЬТР ҮШІН КЕРЕК)
    List<Article> findByTypeOrderByCreatedAtDesc(String type);
}