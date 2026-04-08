package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name = "userId", nullable = false)
    String userId;
    @Column(name = "title", nullable = false)
    String title;
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    String message;
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    boolean isRead = false;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    NotificationStatus status = NotificationStatus.INACTIVE;
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    Instant createdAt = Instant.now();
}
