package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Notification;
import com.devteria.identity_service.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(String userID, NotificationStatus notificationStatus);
}
