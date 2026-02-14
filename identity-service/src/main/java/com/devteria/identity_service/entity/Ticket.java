package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.GroupDashboardStatus;
import com.devteria.identity_service.enums.RequestType;
import com.devteria.identity_service.enums.TicketStatus;
import com.devteria.identity_service.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "ticket_id", nullable = false)
    String ticket_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester", nullable = false)
    User requester;

    @Column(name = "type", length = 255)
    @Enumerated(EnumType.STRING)
    RequestType type;

    @NotNull
    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff", nullable = false)
    User assigned_staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver", nullable = false)
    User approver;

    @Column(name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;
}
