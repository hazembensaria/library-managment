package com.isamm.libraryManagement.service.impl;

import com.isamm.libraryManagement.entity.Notification;
import com.isamm.libraryManagement.entity.NotificationType;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.NotificationRepository;
import com.isamm.libraryManagement.service.EmailService;
import com.isamm.libraryManagement.service.NotificationService;
import com.isamm.libraryManagement.service.WebSocketNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final WebSocketNotificationService webSocketNotificationService;

    // OK (tu as UserRepository si besoin)
    // private final UserRepository userRepository;

    // --- PAS MA TÂCHE (en attente git push) ---
    // private final LoanRepository loanRepository;
    // private final ReservationRepository reservationRepository;
    // private final LoanService loanService;
    // private final ReservationService reservationService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            EmailService emailService,
            WebSocketNotificationService webSocketNotificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @Override
    public Notification envoyerNotification(User user, String message, NotificationType type) {

        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setType(type);
        n.setDateEnvoi(LocalDateTime.now());
        n.setLu(false);

        Notification saved = notificationRepository.save(n);

        // Email (générique)
        String subject = switch (type) {
            case RETARD -> "Retard de prêt";
            case DISPONIBILITE -> "Ressource disponible";
            case RAPPEL -> "Rappel";
        };

        // ⚠️ Ici, il faut envoyer vers l'email.
        // Si getUsername() = email chez toi, ok.
        // Sinon remplace par user.getEmail()
        emailService.sendEmail(user.getUsername(), subject, message);

        // Push WebSocket (simulé) via service dédié (plus besoin de NotificationWSMessage)
        try {
            // username = email ou username selon Spring Security
            webSocketNotificationService.sendNotificationToUser(user.getUsername(), message);

            // Si tu veux notifier tous les bibliothécaires (optionnel) :
            // webSocketNotificationService.sendNotificationToLibrarians(message);
        } catch (Exception ignored) {
            // si WS pas connecté, on ignore: l'email + DB suffisent
        }

        return saved;
    }

    @Override
    public List<Notification> getNotificationsUtilisateur(User user) {
        return notificationRepository.findByUserOrderByDateEnvoiDesc(user);
    }

    @Override
    public void marquerCommeLue(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public long countNonLues(User user) {
        return notificationRepository.countByUserAndLuFalse(user);
    }
}
