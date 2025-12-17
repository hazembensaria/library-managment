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
        String html = buildWowEmail(subject, message);
        emailService.sendHtml(user.getUsername(), subject, html);


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
    
    private String buildWowEmail(String title, String message) {
        return """
    <!doctype html>
    <html lang="fr">
    <head><meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/></head>
    <body style="margin:0;padding:0;background:#f3f4f6;font-family:Arial,Helvetica,sans-serif;">
      <div style="max-width:680px;margin:0 auto;padding:24px;">
        <div style="background:linear-gradient(135deg,#0f172a,#2563eb);border-radius:18px;padding:22px 24px;color:#fff;">
          <div style="font-size:13px;opacity:.9;letter-spacing:.3px;">Library Management System</div>
          <div style="font-size:24px;font-weight:800;margin-top:6px;line-height:1.2;">%s</div>
          <div style="font-size:13px;opacity:.9;margin-top:6px;">Notification automatique</div>
        </div>

        <div style="background:#ffffff;border-radius:18px;padding:22px 24px;margin-top:14px;box-shadow:0 10px 25px rgba(0,0,0,.06);">
          <div style="font-size:14px;line-height:1.7;color:#374151;">
            %s
          </div>

          <div style="margin-top:18px;">
            <a href="#" style="display:inline-block;background:#2563eb;color:#fff;text-decoration:none;padding:12px 16px;border-radius:12px;font-weight:800;font-size:14px;">
              Accéder à la plateforme
            </a>
          </div>
        </div>

        <div style="text-align:center;color:#9ca3af;font-size:12px;margin-top:16px;">
          Bibliothèque ISAMM • Ceci est un message automatique.
        </div>
      </div>
    </body>
    </html>
    """.formatted(escapeHtml(title), escapeHtml(message));
    }

    private String escapeHtml(String input) {
        if (input == null) return "—";
        return input.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
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
