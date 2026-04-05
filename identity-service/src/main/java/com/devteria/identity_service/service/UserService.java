package com.devteria.identity_service.service;
import com.devteria.identity_service.configuration.SecurityConfig;
import com.devteria.identity_service.dto.request.CustomerCreationRequest;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.entity.ForgotPasswordToken;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.DepartmentRepository;
import com.devteria.identity_service.repository.ForgotPasswordTokenRepository;
import com.devteria.identity_service.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    private static final Log log = LogFactory.getLog(UserService.class);
    UserRepository userRepository;
    SystemAuditLogService systemAuditLogService;
    DepartmentRepository departmentRepository;
    EmailSenderService EmailService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    public User createUser(UserCreationRequest userCreationRequest) {
        User user = User.builder()
                .username(userCreationRequest.getUser_name())
                .email(userCreationRequest.getEmail())
                .role(SystemRole.STAFF)
                .createdAt(Instant.now())
                .status(UserStatus.ACTIVE)
                .build();
        systemAuditLogService.logEvent(
                user,
                EventLog.USER_CREATED,
                TargetEntity.USER,
                user.getUser_id()
        );
        return userRepository.save(user);
    }
    public User createCustomUser(CustomerCreationRequest user) {
        User customUser = User.builder()
                .username(user.getUser_name())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(SystemRole.CUSTOMER)
                .createdAt(Instant.now())
                .status(UserStatus.INACTIVE)
                .build();
        User savedUser = userRepository.save(customUser);
        systemAuditLogService.logEvent(
                savedUser,
                EventLog.CUSTOMER_CREATED,
                TargetEntity.USER,
                savedUser.getUser_id()
        );
        return savedUser ;
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
    public User getAdminUser() {
        return userRepository.findByRole(SystemRole.ADMINISTRATOR).stream().findFirst().orElseThrow( () -> new WebException(ErrorCode.USER_NOT_FOUND));
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
    public User updateUserRole(String userID, SystemRole newRole) {
        User user = getUserByID(userID);
        User oldUserSnapshot = new User();
        BeanUtils.copyProperties(user, oldUserSnapshot);
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        try{
            systemAuditLogService.logEntityUpdate(
                    getLoggedInUser(),
                    oldUserSnapshot,
                    updatedUser,
                    userID,
                    TargetEntity.USER,
                    EventLog.USER_ROLE_UPDATED
            );
        } catch (Exception e) {
            log.error("Failed to log user role update", e);
        }
        return user;
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
    @Transactional
    public void generateAndSendOtp(String email) {
        if(email.endsWith("fpt.edu.vn")) {
            throw new WebException(ErrorCode.INVALID_EMAIL_DOMAIN);
        }
        User user = getUserByEmail(email);
        forgotPasswordTokenRepository.findByUser(user).ifPresent(existingToken -> {
            Instant createdAt = existingToken.getExpiryTime().minus(2, ChronoUnit.MINUTES);
            long secondsSinceLastRequest = ChronoUnit.SECONDS.between(createdAt, Instant.now());
            if (secondsSinceLastRequest < 60) {
                throw new WebException(ErrorCode.PLEASE_WAIT_BEFORE_RESENDING_OTP);
            }
        });
        String otp = String.format("%06d", new Random().nextInt(999999));
        ForgotPasswordToken token = forgotPasswordTokenRepository.findByUser(user).orElse(new ForgotPasswordToken());
        token.setUser(user);
        token.setOtpCode(otp);
        token.setExpiryTime(Instant.now().plus(2, ChronoUnit.MINUTES));
        forgotPasswordTokenRepository.save(token);
        try {
            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .subject("Your OTP Code")
                    .body("Your OTP code is: " + otp + ". It will expired in 2 minutes.")
                    .build();
            systemAuditLogService.logEvent(
                    user,
                    EventLog.OTP_GENERATED,
                    TargetEntity.USER,
                    user.getUser_id()
            );
             EmailService.sendEmail(mailBody);
        }catch (MessagingException e){
            throw new WebException(ErrorCode.UNCATEGORIZED);
        }
    }
    @Transactional
    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        User user = getUserByEmail(email);
        ForgotPasswordToken token = forgotPasswordTokenRepository.findByUser(user)
                .orElseThrow(() -> new WebException(ErrorCode.INVALID_OTP));
        if(!token.getOtpCode().equals(otp)) {
            throw new WebException(ErrorCode.INVALID_OTP);
        }
        if(token.getExpiryTime().isBefore(Instant.now())) {
            forgotPasswordTokenRepository.delete(token);
            throw new WebException(ErrorCode.EXPIRED_OTP);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        forgotPasswordTokenRepository.delete(token);
        systemAuditLogService.logEvent(
                user,
                EventLog.PASSWORD_RESET,
                TargetEntity.CUSTOMER,
                user.getUser_id()
        );
    }
}
