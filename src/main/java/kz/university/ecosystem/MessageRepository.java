package kz.university.ecosystem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 1. Екі қолданушы арасындағы хаттарды уақыты бойынша реттеп алу (Диалог үшін)
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
        User sender, User receiver, User receiverInverse, User senderInverse
    );

    // 2. Ең соңғы хабарламаларды алу (Чаттар тізімі үшін)
    // Бұл сұраныс әр адаммен болған соңғы хатты тауып, оны уақыты бойынша реттейді
    @Query(value = "SELECT * FROM messages WHERE id IN (" +
                   "  SELECT MAX(id) FROM messages " +
                   "  WHERE sender_id = :userId OR receiver_id = :userId " +
                   "  GROUP BY (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id))" +
                   ") ORDER BY timestamp DESC", nativeQuery = true)
    List<Message> findLastMessagesByUser(@Param("userId") Long userId);
}