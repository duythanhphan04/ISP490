package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.DepartmentRepository;
import com.devteria.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private static final Log log = LogFactory.getLog(UserService.class);
    UserRepository userRepository;
    SystemAuditLogService systemAuditLogService;
    DepartmentRepository departmentRepository;
    public User createUser(UserCreationRequest userCreationRequest) {
        User user = User.builder()
                .username(userCreationRequest.getUser_name())
                .email(userCreationRequest.getEmail())
                .role(SystemRole.STAFF)
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
    @Transactional
    public User deleteUser(String userID) {
        User user = getUserByID(userID);
        User loggedInUser = getLoggedInUser();
        if(loggedInUser.getUser_id().equals(userID)) {
            throw new WebException(ErrorCode.CANNOT_DELETE_OWN_ACCOUNT);
        }
        userRepository.deleteById(userID);
        systemAuditLogService.logEvent(loggedInUser, EventLog.USER_DELETED, TargetEntity.USER, userID);
        return user;
    }
    @Transactional
    public User updateUserStatus(String userID, UserStatus newStatus) {
        User user = getUserByID(userID);

        if (user.getStatus() == newStatus) {
            return user;
        }
        user.setStatus(newStatus);
        userRepository.save(user);

        EventLog eventLog = (newStatus == UserStatus.INACTIVE)
                ? EventLog.USER_SOFT_DELETED
                : EventLog.USER_RESTORED;

        systemAuditLogService.logEvent(
                getLoggedInUser(),
                eventLog,
                TargetEntity.USER,
                userID
        );
        return user;
    }
    public List<User> getUserByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    public List<User> getUsersByRole(SystemRole role) {
        return userRepository.findByRole(role);
    }
    public User addUserToDepartment(String userID, String departmentID) {
       User user = getUserByID(userID);
       Department department = departmentRepository.findById(departmentID).orElseThrow( () -> new WebException(ErrorCode.DEPARTMENT_NOT_FOUND));
       user.setDepartment(department);
       userRepository.save(user);
       systemAuditLogService.logEvent(
               getLoggedInUser(),
               EventLog.USER_ADDED_TO_DEPARTMENT,
               TargetEntity.USER,
               departmentID
       );
       return user;
    }
    public Department getUserDepartment(String userID) {
        User user = getUserByID(userID);
        return user.getDepartment();
    }
    public User getManagerByUserID(String userID) {
        User user = getUserByID(userID);
        Department department = user.getDepartment();
        if(department == null) {
            throw new WebException(ErrorCode.MANAGER_NOT_FOUND);
        }
        User manager = department.getManager();
        if(manager == null) {
            throw new WebException(ErrorCode.MANAGER_NOT_FOUND);
        }
        return manager;
    }
}
