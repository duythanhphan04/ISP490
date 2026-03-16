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
    MEMBER_ALREADY_DELETED(1012, "Member already deleted from group", HttpStatus.BAD_REQUEST),
    DEPARTMENT_NOT_FOUND(1014, "Department not found", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_OWN_ACCOUNT(1015, "Cannot delete own account", HttpStatus.BAD_REQUEST),
    GROUP_CANNOT_DELETE_HAS_MEMBERS (1016, "Group cannot be deleted because it has members", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_GROUP (1017, "User is already in the group", HttpStatus.BAD_REQUEST),
    DASHBOARD_ALREADY_EXISTS (1018, "Dashboard already exists", HttpStatus.BAD_REQUEST),
    DASHBOARD_NOT_FOUND (1019, "Dashboard not found", HttpStatus.BAD_REQUEST),
    DEPARTMENT_ALREADY_EXISTS (1020, "Department already exists", HttpStatus.BAD_REQUEST),
    GROUP_ALREADY_HAS_DASHBOARD_ACCESS (1021, "Group already has access to the dashboard", HttpStatus.BAD_REQUEST),
    GROUP_DASHBOARD_ACCESS_NOT_FOUND (1022, "Group dashboard access not found", HttpStatus.BAD_REQUEST),
    GROUP_DASHBOARD_ACCESS_ALREADY_GRANTED (1023, "Group already has dashboard access granted", HttpStatus.BAD_REQUEST),
    GROUP_DASHBOARD_ACCESS_ALREADY_REVOKED (1024, "Group dashboard access already revoked", HttpStatus.BAD_REQUEST),
    TICKET_NOT_FOUND(1025, "Ticket not found", HttpStatus.BAD_REQUEST),
    MANAGER_NOT_FOUND (1026, "Manager not found for the user's department", HttpStatus.BAD_REQUEST),
    TICKET_NOT_OPEN (1027, "Ticket is not open", HttpStatus.BAD_REQUEST),
    INVALID_TICKET_STATUS_UPDATE (1028, "Invalid ticket status update", HttpStatus.BAD_REQUEST),
    INVALID_DASHBOARD_STATUS (1030, "Invalid dashboard status update", HttpStatus.BAD_REQUEST),
    MISSING_REJECTION_REASON (1029, "Missing rejection reason for ticket rejection", HttpStatus.BAD_REQUEST);

  private final int code;
  private final String message;
  private final HttpStatusCode httpStatusCode;

  ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }
}
