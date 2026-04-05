package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.ForgotPasswordToken;
import com.devteria.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken,String> {
    Optional<ForgotPasswordToken> findByUser(User user);
}
