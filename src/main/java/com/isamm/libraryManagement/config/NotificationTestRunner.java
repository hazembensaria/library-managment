package com.isamm.libraryManagement.config;

import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.NotificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationTestRunner {

    @Bean
    @ConditionalOnProperty(name = "app.mailtest.enabled", havingValue = "true")
    CommandLineRunner testNotification(NotificationService notificationService,
                                       UserRepository userRepository) {
        return args -> {
            System.out.println(">>> RUNNER MAIL TEST START");

            String email = "test@mailhog.local"; // avec MailHog, n'importe quel email marche

            // ✅ 1) récupérer le user s'il existe, sinon le créer + le sauver
            User u = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setFirstname("Test");
                newUser.setLastname("User");
                newUser.setEmail(email);
                newUser.setPassword("test");
                newUser.setRole(Role.USER);
                return userRepository.save(newUser); // ✅ IMPORTANT: maintenant u a un id
            });

            // ✅ 2) maintenant tu peux créer/sauver Notification sans erreur Hibernate
            notificationService.notifyDisponibilite(u, "Clean Code (Test)");
            notificationService.notifyRappel(u, "Spring Boot (Test)", "20/12/2025");

            System.out.println("✅ Notifications test envoyées (via MailHog)");
            System.out.println("➡️ Ouvre: http://localhost:8025");
        };
    }
}
