package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.SystemAuditLogs;
import com.devteria.identity_service.entity.SystemAuditLogsDetail;
import com.devteria.identity_service.service.SystemAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Builder
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemAuditLogController {
    @Autowired
    SystemAuditLogService systemAuditLogService;
    @GetMapping
    @Operation(summary = "Get all system audit logs")
    public ApiResponse<List<SystemAuditLogs>> getAllLogs() {
        List<SystemAuditLogs> logs = systemAuditLogService.getAllLogs();
        return ApiResponse.<List<SystemAuditLogs>>builder()
                .code(1000)
                .message("Retrieved all system audit logs successfully")
                .data(logs)
                .build();
    }
    @GetMapping("/{logId}/details")
    @Operation(summary = "Get details of a specific audit log")
    public ApiResponse<List<SystemAuditLogsDetail>> getLogDetails( @PathVariable String logId) {
        List<SystemAuditLogsDetail> details = systemAuditLogService.getLogDetails(logId);
        return ApiResponse.<List<SystemAuditLogsDetail>>builder()
                .code(1000)
                .message("Retrieved log details successfully")
                .data(details)
                .build();
    }
}
