package kz.university.ecosystem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    // unique = true қосылды: енді email қайталанбайды
    // nullable = false қосылды: email бос болмауы керек
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String role; // "STUDENT" немесе "TEACHER"
}