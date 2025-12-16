package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.entity.NotificationType;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.UserRepository;
import com.isamm.libraryManagement.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // Test email simple: crée notif + envoie mail
    @PostMapping("/test-email")
    public String testEmail(@RequestParam String message,
                            @AuthenticationPrincipal Object principal) {

        User user = resolveUser(principal);
        notificationService.envoyerNotification(user, message, NotificationType.RAPPEL);

        // retour dashboard (puisque tu veux la boîte dans dashboard)
        return "redirect:/dashboard";
    }

    @PostMapping("/lue/{id}")
    public String lue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return "redirect:/dashboard";
    }

    private User resolveUser(Object principal) {
        if (principal instanceof User u) return u;

        if (principal instanceof UserDetails ud) {
            String usernameOrEmail = ud.getUsername();
            // adapte selon ton UserRepository
            return userRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new RuntimeException("User introuvable: " + usernameOrEmail));
            // si tu utilises email:
            // return userRepository.findByEmail(usernameOrEmail).orElseThrow(...);
        }

        throw new RuntimeException("Principal non supporté");
    }
}
