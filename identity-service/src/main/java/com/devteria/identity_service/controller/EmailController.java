package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.ApiResponse;
import com.devteria.identity_service.entity.MailBody;
import com.devteria.identity_service.response.EmailResponse;
import com.devteria.identity_service.service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Builder
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
  @Autowired EmailSenderService emailSenderService;

  @PostMapping("/sendToEmail/{email}/subject/{subject}/body/{body}")
  @Operation(summary = "Send email to user")
  public ApiResponse<EmailResponse> sendEmail(
      @PathVariable String email, @PathVariable String subject, @PathVariable String body)
      throws MessagingException {
    MailBody mailBody =
        MailBody.builder().to(new String[] {email}).subject(subject).body(body).build();
    emailSenderService.sendEmail(mailBody);
    EmailResponse emailResponse = new EmailResponse();
    emailResponse.setToEmail(email);
    emailResponse.setSubject(subject);
    emailResponse.setBody(body);
    emailResponse.setSent(true);
    return ApiResponse.<EmailResponse>builder()
        .code(1000)
        .message("Email sent successfully")
        .data(emailResponse)
        .build();
  }
  @PostMapping("/sendToEmail/{email}/subject/{subject}")
  @Operation(summary = "Send kit email to user")
  public ApiResponse<EmailResponse> sendKitByEmail(@PathVariable String email, @PathVariable String subject) throws MessagingException {
      String body = "";
      MailBody mailBody =
              MailBody.builder().to(new String[] {email}).subject(subject).body(body).build();
      emailSenderService.sendEmail(mailBody);
      EmailResponse emailResponse = new EmailResponse();
      emailResponse.setToEmail(email);
      emailResponse.setSubject(subject);
      emailResponse.setBody(body);
      emailResponse.setSent(true);
      return ApiResponse.<EmailResponse>builder()
              .code(1000)
              .message("Email sent successfully")
              .data(emailResponse)
              .build();
  }
}
