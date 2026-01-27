package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2SuccessController {
  @GetMapping("/oauth2/success")
  public ApiResponse<?> handleOAuth2Success(@RequestParam("token") String token) {
    return ApiResponse.builder().code(1000).data("Login successful. Token: " + token).build();
  }
}
