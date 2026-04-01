package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.DashboardUsageLogs;
import com.devteria.identity_service.service.DashboardUsageLogsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Builder
@RestController
@RequestMapping("/dashboard-usage-logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardUsageLogController {
    @Autowired
    DashboardUsageLogsService dashboardUsageLogsService;
    @PostMapping("/dashboardID/{dashboardId}")
    public ApiResponse<DashboardUsageLogs> logDashboardUsage(@PathVariable String dashboardId) {
        return ApiResponse.<DashboardUsageLogs>builder()
                .data(dashboardUsageLogsService.logDashboardUsage(dashboardId))
                .code(1000)
                .message("Dashboard usage logged successfully")
                .build();
    }
    @PostMapping("/{logId}/update/duration/{duration}/device/{deviceType}")
    public ApiResponse<DashboardUsageLogs> updateDashboardUsageLog(@PathVariable String logId, @PathVariable int duration, @PathVariable String deviceType) {
        return ApiResponse.<DashboardUsageLogs>builder()
                .data(dashboardUsageLogsService.updateDashboardUsageLog(logId, duration, deviceType))
                .code(1000)
                .message("Dashboard usage log updated successfully")
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('BI')")
    @GetMapping
    @Operation(summary = "Get all dashboard usage logs")
    public ApiResponse<List<DashboardUsageLogs>> getAllDashboardUsageLogs() {
        return ApiResponse.<List<DashboardUsageLogs>>builder()
                .data(dashboardUsageLogsService.findAllDashboardUsageLogs())
                .code(1000)
                .message("All dashboard usage logs fetched successfully")
                .build();
    }
    @GetMapping("/dashboard/{dashboardId}")
    @Operation(summary = "Get all dashboard usage logs for a specific dashboard")
    public ApiResponse<List<DashboardUsageLogs>> getAllDashboardUsageLogsByDashboardID(@PathVariable String dashboardId) {
        return ApiResponse.<List<DashboardUsageLogs>>builder()
                .data(dashboardUsageLogsService.findAllDashboardUsageLogsByDashboardID(dashboardId))
                .code(1000)
                .message("All dashboard usage logs for the specified dashboard fetched successfully")
                .build();
    }
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all dashboard usage logs for a specific user")
    public ApiResponse<List<DashboardUsageLogs>> getAllDashboardUsageLogsByUser(@PathVariable String userId) {
        return ApiResponse.<List<DashboardUsageLogs>>builder()
                .data(dashboardUsageLogsService.findAllDashboardUsageLogsByUser(userId))
                .code(1000)
                .message("All dashboard usage logs for the specified user fetched successfully")
                .build();
    }
}
