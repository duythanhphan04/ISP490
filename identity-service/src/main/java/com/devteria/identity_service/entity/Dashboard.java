package com.devteria.identity_service.entity;

import com.devteria.identity_service.annotation.AuditableField;
import com.devteria.identity_service.enums.DashboardCategory;
import com.devteria.identity_service.enums.DashboardStatus;
import com.devteria.identity_service.enums.DepartmentStatus;
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
@Table(name = "dashboard")
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "dashboard_id", nullable = false)
    String dashboard_id;

    @Size(max = 255)
    @Column(name = "dashboard_name", nullable = false)
    @AuditableField("dashboard_name")
    String dashboard_name;

    @Column(name = "url_path", nullable = false)
    @AuditableField("url_path")
    String url_path;

    @Column(name = "category", length = 255)
    @Enumerated(EnumType.STRING)
    @AuditableField("category")        
    DashboardCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", nullable = true)
    User updatedBy;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    DashboardStatus status;

    @Column(name = "created_at")
    Instant createdAt;
}
