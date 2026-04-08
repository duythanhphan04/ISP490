package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.NotificationRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Notification;
import com.devteria.identity_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @GetMapping("/{userId}")
    public ApiResponse<List<Notification>> getNotificationsForUser(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getHistory(userId);
        return ApiResponse.<List<Notification>>builder()
                .data(notifications)
                .message("Notifications retrieved successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark a notification as read")
    public ApiResponse<Notification> markAsRead(@PathVariable String notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ApiResponse.<Notification>builder()
                .data(notification)
                .message("Notification marked as read")
                .code(1000)
                .build();
    }
    @PostMapping("/send")
    @Operation(summary = "Send a notification to a user")
    public ApiResponse<Void> sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendNotification(
                request.getUserID(),
                request.getTitle(),
                request.getMessage());
        return ApiResponse.<Void>builder()
                .message("Notification sent successfully")
                .code(1000)
                .build();

    }
}
