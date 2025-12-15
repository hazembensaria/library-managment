package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.Notification;
import com.isamm.libraryManagement.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByDateEnvoiDesc(User user);
}
