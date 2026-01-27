package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.httpclient.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    public User createUser(UserCreationRequest userCreationRequest) {
        User user = User.builder()
                .username(userCreationRequest.getUser_name())
                .email(userCreationRequest.getEmail())
                .role(SystemRole.USER)
                .createdAt(Instant.now())
                .department(userCreationRequest.getDepartment())
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserByID(String userID) {
        return userRepository.findById(userID).orElseThrow( () -> new WebException(ErrorCode.USER_NOT_FOUND));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow( () -> new WebException(ErrorCode.USER_NOT_FOUND));
    }
    public User getLoggedInUser() {
        var context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if(authentication!=null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userID = jwt.getSubject();
            return userRepository.findById(userID).orElseThrow( () -> new WebException(ErrorCode.USER_NOT_FOUND));
        }
        return null;
    }
    public User deleteUser(String userID) {
        User user = getUserByID(userID);
        if(user != null) {
            userRepository.deleteById(userID);
        }
        return user;
    }
    public User updateUserStatus(String userID, UserStatus userStatus) {
        User user = getUserByID(userID);
        if(user != null) {
            user.setStatus(userStatus);
            return userRepository.save(user);
        }
        return null;
    }
    public List<User> getUserByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    public List<User> getUsersByRole(SystemRole role) {
        return userRepository.findByRole(role);
    }

}
