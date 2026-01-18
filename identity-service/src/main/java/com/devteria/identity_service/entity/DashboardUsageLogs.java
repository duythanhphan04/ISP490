package com.devteria.identity_service.entity;

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
@Table(name = "dashboard_usage_logs")
public class DashboardUsageLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "log_id", nullable = false)
    String log_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    Dashboard dashboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "accessed_at")
    Instant viewed_at;

    @Column(name = "session_id", nullable = false)
    String session_id;

    @Column(name = "duration", nullable = false)
    int duration;

    @Column(name = "device_type", nullable = false)
    String device_type;
}
