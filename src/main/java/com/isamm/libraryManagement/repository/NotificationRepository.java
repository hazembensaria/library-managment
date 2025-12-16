package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Notification;
import com.isamm.libraryManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByDateEnvoiDesc(User user);

    long countByUserAndLuFalse(User user);
}
