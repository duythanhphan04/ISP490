package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "User")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "user_id", nullable = false)
    String user_id;

    @NotNull
    @Column(name = "user_name", nullable = false)
    String username;

    @Size(max = 255)
    @Column(name = "email")
    String email;

    @Column(name = "system_role", length = 255)
    @Enumerated(EnumType.STRING)
    SystemRole role;

    @Column(name = "created_at")
    Instant createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id", referencedColumnName = "department_id")
    Department department;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    UserStatus status;
}
