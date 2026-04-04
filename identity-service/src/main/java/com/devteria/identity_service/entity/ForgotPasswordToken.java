package com.devteria.identity_service.entity;

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
@Table(name = "forgot_password_token")
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "department_id", nullable = false)
    String id;

    @Column(name = "otp_code", length = 6, nullable = false)
    String otpCode;

    @Column(name = "expiry_time", nullable = false)
    private Instant expiryTime;

    // Quan hệ 1-1 với bảng User
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    User user;
}
