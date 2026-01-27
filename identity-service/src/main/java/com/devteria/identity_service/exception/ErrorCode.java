package com.devteria.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

  USER_NOT_FOUND(1001, "User not found", HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS(1002, "Username already exists", HttpStatus.NOT_FOUND),
  INVALID_KEY(1003, "Invalid message key", HttpStatus.BAD_REQUEST),
  UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  UNCATEGORIZED(1005, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
  BLOCKED_USER(1006, "User is blocked", HttpStatus.FORBIDDEN),
  UNAUTHORIZED(1007, "Unauthorized: Access is denied", HttpStatus.FORBIDDEN),
  INVALID_ROLE(1008, "Invalid role", HttpStatus.FORBIDDEN),
  GROUP_NOT_FOUND(1009, "Group not found", HttpStatus.BAD_REQUEST),
  WRONG_CREDENTIALS(1010, "Wrong credentials", HttpStatus.UNAUTHORIZED),
  USER_NOT_IN_GROUP(1011, "User not in group", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_DELETED_YET (1013, "Member not deleted yet from group", HttpStatus.BAD_REQUEST),
    MEMBER_ALREADY_DELETED(1012, "Member already deleted from group", HttpStatus.BAD_REQUEST);



  private final int code;
  private final String message;
  private final HttpStatusCode httpStatusCode;

  ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }
}
