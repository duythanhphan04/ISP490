package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.TargetEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "system_audit_logs")
public class SystemAuditLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "log_id", nullable = false)
    String log_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    EventLog action;

    @Column(name = "target_entity", nullable = false)
    @Enumerated(EnumType.STRING)
    TargetEntity target_entity;

    @Column(name = "target_id", length = 255)
    String target_id;

    @Column(name = "timestamp", nullable = false)
    Instant created_at;
}
