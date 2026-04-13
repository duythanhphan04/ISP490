package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.RegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, String> {
    Optional<RegistrationToken>findByEmail(String email);
}
