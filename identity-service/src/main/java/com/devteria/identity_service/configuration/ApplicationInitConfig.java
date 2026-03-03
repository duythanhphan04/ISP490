package com.devteria.identity_service.configuration;

import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import java.time.Instant;
import com.devteria.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
  @Autowired PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner applicationRunner(UserRepository userRepository) {
    return args -> {
      if (userRepository.findByRole(SystemRole.ADMINISTRATOR).isEmpty()) {
        User user =
            User.builder()
                .username("admin")
                .role(SystemRole.ADMINISTRATOR)
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .email("duyhcm04@gmail.com")
                .username("Phan Thanh Duy")
                .build();
        userRepository.save(user);
        log.warn(
            "Admin has been created!");
      }
    };
  }
}
