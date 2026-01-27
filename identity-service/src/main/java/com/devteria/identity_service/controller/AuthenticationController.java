package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.IntrospectRequest;
import com.devteria.identity_service.dto.request.LogoutRequest;
import com.devteria.identity_service.dto.request.RefrestTokenRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.dto.response.AuthenticationResponse;
import com.devteria.identity_service.dto.response.IntrospectResponse;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@Builder
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
  AuthenticationService authenticationService;

  @PostMapping("/outbound/authenticate")
  @Operation(summary = "Authenticate user and return JWT token for outbound services")
  ApiResponse<AuthenticationResponse> outboundAuthenticate(
      @Parameter(
              description = "Authorization code từ Google redirect",
              in = ParameterIn.QUERY,
              required = true)
          @RequestParam("code")
          String code) {
    var result = authenticationService.outboundAuthenticate(code);
    return ApiResponse.<AuthenticationResponse>builder()
        .code(1000)
        .message("User authenticated successfully")
        .data(result)
        .build();
  }

  @PostMapping("/token")
  @Operation(summary = "Login user and return JWT token")
  ApiResponse<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request) {
    AuthenticationResponse result = authenticationService.authenticate(request);
    return ApiResponse.<AuthenticationResponse>builder()
        .code(1000)
        .message("User authenticated successfully")
        .data(result)
        .build();
  }

  @PostMapping("/introspect")
  @Operation(summary = "Introspect JWT token")
  ApiResponse<IntrospectResponse> authentication(@RequestBody IntrospectRequest request)
      throws ParseException, JOSEException {
    var result = authenticationService.introspect(request);
    return ApiResponse.<IntrospectResponse>builder().data(result).build();
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout user and invalidate JWT token")
  ApiResponse<Void> logout(@RequestBody LogoutRequest request)
      throws ParseException, JOSEException {
    authenticationService.logout(request);
    return ApiResponse.<Void>builder().build();
  }

  @PostMapping("/refresh")
  @Operation(summary = "Refresh JWT token")
  ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefrestTokenRequest request)
      throws ParseException, JOSEException {
    AuthenticationResponse result = authenticationService.refreshToken(request);
    return ApiResponse.<AuthenticationResponse>builder()
        .code(1000)
        .message("Token refreshed successfully")
        .data(result)
        .build();
  }
}
