package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.DashboardCreationRequest;
import com.devteria.identity_service.dto.request.DashboardUpdateRequest;
import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.DashboardCategory;
import com.devteria.identity_service.enums.DashboardStatus;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.DashboardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class DashboardService {
    DashboardRepository dashboardRepository;
    UserService userService;
    SystemAuditLogService systemAuditLogService;
    @Transactional // 1. Đảm bảo an toàn dữ liệu
    public Dashboard createDashboard(DashboardCreationRequest request){
        User loggedInUser = userService.getLoggedInUser();
        if(dashboardRepository.existsByDashboardName(request.getDashboard_name())){
            throw new WebException(ErrorCode.DASHBOARD_ALREADY_EXISTS);
        }
        Dashboard dashboard = Dashboard.builder()
                .dashboard_name(request.getDashboard_name())
                .url_path(request.getUrl_path())
                .category(request.getCategory())
                .createdBy(loggedInUser)
                .status(DashboardStatus.ACTIVE) // Nên set trạng thái mặc định
                .build();

        dashboard = dashboardRepository.save(dashboard);
        systemAuditLogService.logEvent(
                loggedInUser,
                EventLog.DASHBOARD_CREATED,
                TargetEntity.DASHBOARD,
                dashboard.getDashboard_id()
        );

        return dashboard;
    }
    public Dashboard getDashboardByName(String dashboardName){
        return dashboardRepository.findByName(dashboardName);
    }
    public Dashboard getDashboardById(String dashboardId){
        return dashboardRepository.findById(dashboardId).orElseThrow( () -> new WebException(ErrorCode.DASHBOARD_NOT_FOUND));
    }
    public List<Dashboard> getAllDashboards(){
        return dashboardRepository.findAll();
    }
    public List<Dashboard> getDashboardsByCreatedBy(String userId){
        User user = userService.getUserByID(userId);
        return dashboardRepository.findAllByCreatedBy(user);
    }
    public List<Dashboard> getDashboardsByCategory(DashboardCategory category){
        return dashboardRepository.findAllByCategory(category);
    }
    public Dashboard deleteDashboardById(String dashboardId){
        User loggedInUser = userService.getLoggedInUser();
        Dashboard dashboard = getDashboardById(dashboardId);
        dashboardRepository.delete(dashboard);
        systemAuditLogService.logEvent(loggedInUser, EventLog.DASHBOARD_DELETED, TargetEntity.DASHBOARD,dashboardId);
        return dashboard;
    }
    public Dashboard softDeleteDashboardById(String dashboardId){
        User loggedInUser = userService.getLoggedInUser();
        Dashboard dashboard = getDashboardById(dashboardId);
        dashboard.setStatus(DashboardStatus.INACTIVE);
        dashboardRepository.save(dashboard);
        systemAuditLogService.logEvent(loggedInUser, EventLog.DASHBOARD_SOFT_DELETED, TargetEntity.DASHBOARD,dashboardId);
        return dashboard;
    }
    public Dashboard restoreDashboardById(String dashboardId){
        User loggedInUser = userService.getLoggedInUser();
        Dashboard dashboard = getDashboardById(dashboardId);
        dashboard.setStatus(DashboardStatus.ACTIVE);
        dashboardRepository.save(dashboard);
        systemAuditLogService.logEvent(loggedInUser, EventLog.DASHBOARD_RESTORED, TargetEntity.DASHBOARD,dashboardId);
        return dashboard;
    }
    @Transactional
    public Dashboard updateDashboard(String dashboardId, DashboardUpdateRequest request){
        User loggedInUser = userService.getLoggedInUser();
        Dashboard dashboard = getDashboardById(dashboardId);
        Dashboard oldDashboardSnapshot = Dashboard.builder()
                .dashboard_name(dashboard.getDashboard_name())
                .url_path(dashboard.getUrl_path())
                .category(dashboard.getCategory())
                .createdBy(dashboard.getCreatedBy())
                .status(dashboard.getStatus())
                .build();
        if(request.getDashboardName() != null) {
            dashboard.setDashboard_name(request.getDashboardName());
        }
        if(request.getUrlPath() != null) {
            dashboard.setUrl_path(request.getUrlPath());
        }
        if(request.getDashboardCategory() != null) {
            dashboard.setCategory(request.getDashboardCategory());
        }
        dashboard.setUpdatedBy(loggedInUser);
        Dashboard updatedDashboard = dashboardRepository.save(dashboard);
        systemAuditLogService.logEntityUpdate(
                userService.getLoggedInUser(),
                oldDashboardSnapshot,
                updatedDashboard,
                dashboardId,
                TargetEntity.DASHBOARD,
                EventLog.DASHBOARD_UPDATED
        );
        return updatedDashboard;
    }

    public List<Dashboard> getDashboardsByStatus(DashboardStatus status) {
        return dashboardRepository.findAllByStatus(status);
    }
}
