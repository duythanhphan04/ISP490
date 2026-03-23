package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.DashboardUsageLogs;
import com.devteria.identity_service.service.DashboardUsageLogsService;
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
}
