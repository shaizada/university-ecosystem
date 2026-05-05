package kz.university.ecosystem;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; 

    @Column(columnDefinition = "TEXT")
    private String content; 

    private LocalDateTime createdAt; 

    // ЖАҢА ӨРІС: Контент түрін ажырату үшін (ARTICLE, NEWS, POLL)
    private String type; 

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author; 

    public Article() {}

    // Getter және Setter-лер
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Түрі үшін Getter және Setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
}