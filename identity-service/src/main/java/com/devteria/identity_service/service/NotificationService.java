package com.devteria.identity_service.service;
import com.devteria.identity_service.entity.Notification;
import com.devteria.identity_service.enums.NotificationStatus;
import com.devteria.identity_service.repository.NotificationRepository;
import com.pusher.rest.Pusher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    Pusher pusher;
    NotificationRepository notificationRepository;
    public void sendNotification(String userID, String title, String message){
        Notification notification = Notification.builder()
                .userId(userID)
                .title(title)
                .message(message)
                .build();
        Notification saved =notificationRepository.save(notification);
        Map<String,String> data = new HashMap<>();
        data.put("id", saved.getId());
        data.put("userID",userID);
        data.put("title",title);
        data.put("message",message);
        data.put("createdAt",saved.getCreatedAt().toString());
        pusher.trigger("user-"+userID,"new-notification",data);
    }
    public List<Notification> getHistory(String userID){
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userID, NotificationStatus.INACTIVE);
    }
    public Notification markAsRead(String notificationID){
        Notification notification = notificationRepository.findById(notificationID)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
