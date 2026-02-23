package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByRole(SystemRole role);

}
