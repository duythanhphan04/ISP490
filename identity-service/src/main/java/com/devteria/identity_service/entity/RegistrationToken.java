package com.devteria.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "registration_token")
public class RegistrationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    String id;

    @Column(name = "email", length = 6, nullable = false)
    String email;

    @Column(name = "otp_code", length = 6, nullable = false)
    String otpCode;

    @Column(name = "expiry_time", nullable = false)
    LocalDateTime expiryTime;
}
