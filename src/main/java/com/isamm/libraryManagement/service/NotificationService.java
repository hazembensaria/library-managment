package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.entity.Notification;
import com.isamm.libraryManagement.entity.NotificationType;
import com.isamm.libraryManagement.entity.User;

import java.util.List;

public interface NotificationService {

    Notification envoyerNotification(User user, String message, NotificationType type);

    List<Notification> getNotificationsUtilisateur(User user);

    void marquerCommeLue(Long notificationId);

    long countNonLues(User user);
}
