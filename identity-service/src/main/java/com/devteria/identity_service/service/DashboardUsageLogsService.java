package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.DashboardUsageLogs;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.DashboardUsageLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardUsageLogsService {
    DashboardUsageLogRepository dashboardUsageLogRepository;
    DashboardService dashboardService;
    UserService userService;
    public DashboardUsageLogs logDashboardUsage(String dashboardId) {
        DashboardUsageLogs log = new DashboardUsageLogs();
        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        log.setUser(userService.getLoggedInUser());
        log.setDashboard(dashboard);
        log.setViewed_at(Instant.now());
        log.setDuration(0);
        log.setDevice_type("Unknown");
        dashboardUsageLogRepository.save(log);
        return log;
    }
    public DashboardUsageLogs updateDashboardUsageLog(String logId, int duration, String deviceType) {
        DashboardUsageLogs log = findById(logId);
        log.setDuration(duration);
        log.setDevice_type(deviceType);
        dashboardUsageLogRepository.save(log);
        return log;
    }
    public DashboardUsageLogs findById(String logId) {
        return dashboardUsageLogRepository.findById(logId)
                .orElseThrow(() -> new WebException(ErrorCode.DASHBOARD_USAGE_LOG_NOT_FOUND));
    }
    public List<DashboardUsageLogs> findAllDashboardUsageLogs() {
        return dashboardUsageLogRepository.findAll();
    }
    public List<DashboardUsageLogs> findAllDashboardUsageLogsByDashboardID(String dashboardId) {
        return dashboardUsageLogRepository.findAllByDashboardId(dashboardId);
    }
    public List<DashboardUsageLogs> findAllDashboardUsageLogsByUser(String userId) {
        return dashboardUsageLogRepository.findAllByUserId(userId);

    }
}
