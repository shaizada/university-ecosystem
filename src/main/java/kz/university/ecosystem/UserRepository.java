package kz.university.ecosystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Email арқылы іздеу (Логин жасау үшін қажет)
    User findByEmail(String email);
}