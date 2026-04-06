package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.dto.response.EmailResponse;
import com.devteria.identity_service.service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController // Đã bỏ @Builder
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailSenderService emailSenderService;
    @PostMapping("/send")
    @Operation(summary = "Send email to user")
    public ApiResponse<EmailResponse> sendEmail(@RequestBody MailBody mailBody) throws MessagingException {
        emailSenderService.sendEmail(mailBody);
        EmailResponse emailResponse = new EmailResponse();
        emailResponse.setToEmail(Arrays.toString(mailBody.to()));
        emailResponse.setSubject(mailBody.subject());
        emailResponse.setBody(mailBody.body());
        emailResponse.setSent(true);
        return ApiResponse.<EmailResponse>builder()
                .code(1000)
                .message("Email sent successfully")
                .data(emailResponse)
                .build();
    }
}