package kz.university.ecosystem;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // Хабарлама жіберуші

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver; // Хабарлама алушы

    @Column(columnDefinition = "TEXT")
    private String content; // Хабарлама мәтіні

    private LocalDateTime timestamp; // Жіберілген уақыты

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now(); // Автоматты түрде қазіргі уақытты қояды
    }
}