package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.GroupStatus;
import com.devteria.identity_service.enums.GroupType;
import com.devteria.identity_service.enums.SystemRole;
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
@Table(name = "group_info")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "group_id", nullable = false)
    String group_id;

    @NotNull
    @Column(name = "user_name", nullable = false)
    String group_name;

    @NotNull
    @Column(name = "description", nullable = false)
    String description;

    @NotNull
    @Column(name = "member", nullable = false)
    int member;

    @Column(name = "group_type", length = 255)
    @Enumerated(EnumType.STRING)
    GroupType group_type;

    @Column(name = "group_status", length = 255)
    @Enumerated(EnumType.STRING)
    GroupStatus status;

    @Column(name = "created_at")
    Instant createdAt;
}
