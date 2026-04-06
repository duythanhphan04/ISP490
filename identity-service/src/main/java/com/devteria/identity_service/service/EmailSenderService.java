package com.devteria.identity_service.service;
import com.devteria.identity_service.entity.MailBody;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSenderService {
    final RestTemplate restTemplate;
    @Value("${brevo.api.key}")
    String apiKey;
    @Value("${brevo.sender.email}")
    String senderEmail;
    @Value("${brevo.sender.name}")
    String senderName;
    @Async
    public void sendEmail(MailBody mailBody) {
        String url = "https://api.brevo.com/v3/smtp/email";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("api-key", apiKey);
        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", senderName, "email", senderEmail));
        List<Map<String, String>> toList = java.util.Arrays.stream(mailBody.to())
                .map(email -> Map.of("email", email))
                .toList();
        body.put("to", toList);
        body.put("subject", mailBody.subject());
        body.put("textContent", mailBody.body());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            System.out.println("Đã bắn email qua Brevo API thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi mail qua API: " + e.getMessage());
        }
    }
}
