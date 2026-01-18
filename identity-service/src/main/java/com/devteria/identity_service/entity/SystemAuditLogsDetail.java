package com.devteria.identity_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "system_audit_logs_detail")
public class SystemAuditLogsDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    SystemAuditLogs systemAuditLogs;

    @Column(name = "column_name", length = 255)
    String column_name;

    @Column(name = "old_value", columnDefinition = "TEXT")
    String old_value;

    @Column(name = "new_value", columnDefinition = "TEXT")
    String new_value;
}
