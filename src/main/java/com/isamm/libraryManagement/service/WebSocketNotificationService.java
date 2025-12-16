package com.isamm.libraryManagement.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envoie une notification WebSocket à un utilisateur spécifique.
     * username = email ou username selon ta config Spring Security.
     * Le client écoute: /user/queue/notifications
     */
    public void sendNotificationToUser(String username, String message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                new NotificationMessage(message)
        );
    }

    /**
     * Envoie une notification à tous les bibliothécaires (topic général).
     * Le client écoute: /topic/librarians
     */
    public void sendNotificationToLibrarians(String message) {
        messagingTemplate.convertAndSend(
                "/topic/librarians",
                new NotificationMessage(message)
        );
    }

    /**
     * Message WebSocket simple (pas besoin de NotificationWSMessage).
     */
    public static class NotificationMessage {
        private String content;
        private String timestamp;

        public NotificationMessage() {
            this.timestamp = LocalDateTime.now().toString();
        }

        public NotificationMessage(String content) {
            this.content = content;
            this.timestamp = LocalDateTime.now().toString();
        }

        public String getContent() {
            return content;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
