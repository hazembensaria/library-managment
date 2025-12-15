package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.entity.Notification;
import com.isamm.libraryManagement.entity.NotificationType;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.NotificationRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepo, EmailService emailService) {
        this.notificationRepo = notificationRepo;
        this.emailService = emailService;
    }

    public void notifyDisponibilite(User user, String titre) {
        emailService.sendDisponibilite(user.getUsername(), titre);
        save(user, NotificationType.DISPONIBILITE, "Votre ressource \"" + titre + "\" est disponible.");
    }

    public void notifyRappel(User user, String titre, String dateRetourPrevu) {
        emailService.sendRappelRetour(user.getUsername(), titre, dateRetourPrevu);
        save(user, NotificationType.RAPPEL, "Rappel retour : \"" + titre + "\" avant " + dateRetourPrevu);
    }

    public void save(User user, NotificationType type, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setMessage(message);
        n.setDateEnvoi(LocalDateTime.now());
        n.setLu(false);
        notificationRepo.save(n);
    }
}
