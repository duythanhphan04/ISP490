package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.ChangePasswordRequest;
import com.devteria.identity_service.dto.request.CustomerCreationRequest;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.service.SystemAuditLogService;
import com.devteria.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user with the provided information")
    public ApiResponse<User> createUser(@RequestBody UserCreationRequest request) {
        User user = userService.createUser(request);
        return ApiResponse.<User>builder()
                .data(user)
                .message("User created successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/custom")
    @Operation(summary = "Create a new customer ", description = "Create a new customer with the provided information")
    public ApiResponse<User> createCustomUser(@RequestBody CustomerCreationRequest request) {
        User user = userService.createCustomUser(request);
        return ApiResponse.<User>builder()
                .data(user)
                .message("Custom user created successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/role/{userID}/{role}")
    @Operation(summary = "Update user's role")
    public ApiResponse<User> updateUserRole(@PathVariable String userID, @PathVariable SystemRole role) {
        User updatedUser = userService.updateUserRole(userID, role);
        return ApiResponse.<User>builder()
                .data(updatedUser)
                .message("User role updated successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('BI')")
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponse.<List<User>>builder()
                .data(users)
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/{userID}")
    @Operation(summary = "Get user by ID")
    ApiResponse<User> getUserByID(@PathVariable String userID) {
        return ApiResponse.<User>builder()
                .data(userService.getUserByID(userID))
                .message("User fetched successfully")
                .code(1000)
                .build();

    }
    @PreAuthorize( "hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{userID}")
    @Operation(summary = "Delete user by ID")
    ApiResponse<User> deleteUserByID(@PathVariable String userID) {
        return ApiResponse.<User>builder()
                .data(userService.deleteUser(userID))
                .message("User deleted successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/my-profile")
    @Operation(summary = "Get logged-in user's profile")
    ApiResponse<User> getLoggedInUserProfile() {
        User loggedInUser = userService.getLoggedInUser();
        return ApiResponse.<User>builder()
                .data(loggedInUser)
                .message("Logged-in user's profile fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/soft-delete/{userID}")
    @Operation(summary = "Soft delete user by ID")
    ApiResponse<User> softDeleteUserByID(@PathVariable String userID) {
        return ApiResponse.<User>builder()
                .data(userService.updateUserStatus(userID, UserStatus.INACTIVE))
                .message("User soft deleted successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/restore/{userID}")
    @Operation(summary = "Restore soft-deleted user by ID")
    ApiResponse<User> restoreUserByID(@PathVariable String userID) {
        return ApiResponse.<User>builder()
                .data(userService.updateUserStatus(userID, UserStatus.ACTIVE))
                .message("User restored successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role")
    ApiResponse<List<User>> getUsersByRole(@PathVariable SystemRole role) {
        return ApiResponse.<List<User>>builder()
                .data(userService.getUsersByRole(role))
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status")
    ApiResponse<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        return ApiResponse.<List<User>>builder()
                .data(userService.getUserByStatus(status))
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    ApiResponse<User> getUserByEmail(@PathVariable String email) {
        return ApiResponse.<User>builder()
                .data(userService.getUserByEmail(email))
                .message("User fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{userID}/department/{departmentID}")
    @Operation(summary = "Add user to department")
    ApiResponse<User> addUserToDepartment(@PathVariable String userID, @PathVariable String departmentID) {
        return ApiResponse.<User>builder()
                .data(userService.addUserToDepartment(userID, departmentID))
                .message("User's department updated successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP to user's email for password reset")
    ApiResponse<Void> sendOtp(@RequestParam String email) {
        userService.generateAndSendOtp(email);
        return ApiResponse.<Void>builder()
                .message("OTP sent successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/reset-password")
    @Operation(summary = "Reset user's password using OTP")
    ApiResponse<Void> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        userService.verifyOtpAndResetPassword(email, otp, newPassword);
        return ApiResponse.<Void>builder()
                .message("Password reset successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/role/{role}/status/{status}")
    @Operation(summary = "Get users by role and status")
    ApiResponse<List<User>> getUsersByRoleAndStatus(@PathVariable SystemRole role, @PathVariable UserStatus status) {
        return ApiResponse.<List<User>>builder()
                .data(userService.getUserByRoleAndStatus(role, status))
                .message("Users fetched successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/change-password")
    @Operation(summary = "Change logged-in user's password")
    ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .message("Password changed successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP for password reset")
    ApiResponse<Void> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        userService.verifyOtp(email, otp);
        return ApiResponse.<Void>builder()
                .message("OTP verified successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/generate-registration-otp")
    @Operation(summary = "Generate OTP for user registration")
    ApiResponse<Void> generateRegistrationOtp(@RequestParam String email) {
        userService.generateRegistrationOtpAndSendEmail(email);
        return ApiResponse.<Void>builder()
                .message("Registration OTP generated and sent successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/verify-registration-otp")
    @Operation(summary = "Verify OTP for user registration")
    ApiResponse<Void> verifyRegistrationOtp(@RequestParam String email, @RequestParam String otp) {
        userService.verifyRegistrationOtp(email, otp);
        return ApiResponse.<Void>builder()
                .message("Registration OTP verified successfully")
                .code(1000)
                .build();
    }
}
