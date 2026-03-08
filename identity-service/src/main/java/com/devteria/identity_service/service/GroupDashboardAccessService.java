package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.GroupDashboardAccess;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.GroupDashboardStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.GroupDashboardAccessRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupDashboardAccessService {
    GroupService groupService;
    DashboardService dashboardService;
    GroupDashboardAccessRepository groupDashboardAccessRepository;
    UserService userService;
    SystemAuditLogService systemAuditLogService;
    @Transactional
    public GroupDashboardAccess addDashboardToGroup(String groupId, String dashboardId) {
        User loggedInUser = userService.getLoggedInUser();
        Group group = groupService.getGroupByID(groupId);
        Dashboard dashboard = dashboardService.getDashboardById(dashboardId);
        boolean exists = groupDashboardAccessRepository.existsByGroupAndDashboard(group, dashboard);
        if (exists) {
            throw new WebException(ErrorCode.GROUP_ALREADY_HAS_DASHBOARD_ACCESS);
        }
        GroupDashboardAccess groupDashboardAccess = GroupDashboardAccess.builder()
                .group(group)
                .dashboard(dashboard)
                .status(GroupDashboardStatus.INACTIVE)
                .grantedBy(loggedInUser)
                .build();
        GroupDashboardAccess savedGroupDashboardAccess = groupDashboardAccessRepository.save(groupDashboardAccess);
        systemAuditLogService.logEvent(
                loggedInUser,
                com.devteria.identity_service.enums.EventLog.DASHBOARD_ACCESS_GRANTED_TO_GROUP,
                com.devteria.identity_service.enums.TargetEntity.GROUP,
                groupId
        );
        return savedGroupDashboardAccess;
    }
    public List<Dashboard> getDashboardsByGroupID (String groupId) {
        return groupDashboardAccessRepository.findAllDashboardsByGroupId(groupId);
    }
    public List<Group> getGroupsByDashboardID (String dashboardId) {
        return groupDashboardAccessRepository.findAllGroupsByDashboardId(dashboardId);
    }
    @Transactional
    public GroupDashboardAccess updateGroupDashboardAccessStatus(String groupDashboardId, GroupDashboardStatus newStatus) {
        GroupDashboardAccess groupDashboardAccess = groupDashboardAccessRepository.findById(groupDashboardId)
                .orElseThrow(() -> new WebException(ErrorCode.GROUP_DASHBOARD_ACCESS_NOT_FOUND));
        GroupDashboardStatus oldStatus = groupDashboardAccess.getStatus();
        if(newStatus == GroupDashboardStatus.INACTIVE && oldStatus == GroupDashboardStatus.INACTIVE) {
            throw new WebException(ErrorCode.GROUP_DASHBOARD_ACCESS_ALREADY_GRANTED);
        }
        if(newStatus == GroupDashboardStatus.ACTIVE && oldStatus == GroupDashboardStatus.ACTIVE) {
            throw new WebException(ErrorCode.GROUP_DASHBOARD_ACCESS_ALREADY_REVOKED);
        }
        groupDashboardAccess.setStatus(newStatus);
        GroupDashboardAccess updatedGroupDashboardAccess = groupDashboardAccessRepository.save(groupDashboardAccess);
        if(newStatus == GroupDashboardStatus.ACTIVE) {
            systemAuditLogService.logEvent(
                    userService.getLoggedInUser(),
                    com.devteria.identity_service.enums.EventLog.DASHBOARD_ACCESS_REVOKED_FROM_GROUP,
                    com.devteria.identity_service.enums.TargetEntity.GROUP,
                    groupDashboardAccess.getGroup().getGroup_id()
            );
        } else {
            systemAuditLogService.logEvent(
                    userService.getLoggedInUser(),
                    com.devteria.identity_service.enums.EventLog.DASHBOARD_ACCESS_GRANTED_TO_GROUP,
                    com.devteria.identity_service.enums.TargetEntity.GROUP,
                    groupDashboardAccess.getGroup().getGroup_id()
            );
        }
        return updatedGroupDashboardAccess;
    }
    public GroupDashboardAccess getGroupDashboardAccessByGroupAndDashboard(String groupId, String dashboardId) {
        return groupDashboardAccessRepository.findByGroupAndDashboard(groupId, dashboardId)
                .orElseThrow(() -> new WebException(ErrorCode.GROUP_DASHBOARD_ACCESS_NOT_FOUND));
    }

}
