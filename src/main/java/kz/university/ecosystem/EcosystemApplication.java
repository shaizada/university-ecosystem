package kz.university.ecosystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class EcosystemApplication {

    private static final Logger log = LoggerFactory.getLogger(EcosystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EcosystemApplication.class, args);
    }

    // Жоба іске қосылып дайын болғанда осы метод жұмыс істейді
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("\n----------------------------------------------------------");
        System.out.println("   University Ecosystem жобасы іске қосылды!");
        System.out.println("   Сілтеме бойынша өт: http://localhost:8080/register");
        System.out.println("----------------------------------------------------------\n");
    }
}