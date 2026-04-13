package com.devteria.identity_service.controller;
import com.devteria.identity_service.dto.request.DashboardCreationRequest;
import com.devteria.identity_service.dto.request.DashboardUpdateRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.GroupDashboardAccess;
import com.devteria.identity_service.enums.DashboardCategory;
import com.devteria.identity_service.enums.DashboardStatus;
import com.devteria.identity_service.enums.GroupDashboardStatus;
import com.devteria.identity_service.service.DashboardService;
import com.devteria.identity_service.service.GroupDashboardAccessService;
import com.devteria.identity_service.service.UserService;
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
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {
    @Autowired
    DashboardService dashboardService;
    @Autowired
    UserService userService;
    @Autowired
    GroupDashboardAccessService groupDashboardAccessService;
    @PreAuthorize("hasRole('BI')")
    @PostMapping
    @Operation(summary = "Create a new dashboard")
    public ApiResponse<Dashboard> createDashboard( @RequestBody DashboardCreationRequest request) {
        Dashboard dashboard = dashboardService.createDashboard(request);
        return ApiResponse.<Dashboard>builder()
                .data(dashboard)
                .message("Dashboard created successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @GetMapping
    @Operation(summary = "Get all dashboard")
    public ApiResponse<List<Dashboard>> getAllDashboards() {
        List<Dashboard> dashboard = dashboardService.getAllDashboards();
        return ApiResponse.<List<Dashboard>>builder()
                .data(dashboard)
                .message("Dashboards fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/{dashboardID}")
    @Operation(summary = "Get dashboard by ID")
    ApiResponse<Dashboard> getDashboardByID(@PathVariable String dashboardID) {
        return ApiResponse.<Dashboard>builder()
                .data(dashboardService.getDashboardById(dashboardID))
                .message("Dashboard fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/by-category/{category}")
    @Operation(summary = "Get dashboards by category")
    ApiResponse<List<Dashboard>> getDashboardsByCategory(@PathVariable DashboardCategory category) {
        return ApiResponse.<List<Dashboard>>builder()
                .data(dashboardService.getDashboardsByCategory(category))
                .message("Dashboards fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/by-creator/{userID}")
    @Operation(summary = "Get dashboards by creator user ID")
    ApiResponse<List<Dashboard>> getDashboardsByCreatedBy(@PathVariable String userID){
        return ApiResponse.<List<Dashboard>>builder()
                .data(dashboardService.getDashboardsByCreatedBy(userID))
                .message("Dashboards fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @DeleteMapping("/{dashboardID}")
    @Operation(summary = "Delete dashboard by ID")
    ApiResponse<Dashboard> deleteDashboardByID(@PathVariable String dashboardID) {
        return ApiResponse.<Dashboard>builder()
                .data(dashboardService.deleteDashboardById(dashboardID))
                .message("Dashboard deleted successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/status/{status}")
    @Operation(summary = "Get dashboards by status")
    public ApiResponse<List<Dashboard>> getDashboardsByStatus(@PathVariable DashboardStatus status){
        List<Dashboard> dashboards = dashboardService.getDashboardsByStatus(status);
        return ApiResponse.<List<Dashboard>>builder()
                .data(dashboards)
                .message("Dashboards fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/soft-delete/{dashboardID}")
    @Operation(summary = "Soft delete dashboard by ID")
    ApiResponse<Dashboard> softDeleteDashboardByID(@PathVariable String dashboardID) {
        return ApiResponse.<Dashboard>builder()
                .data(dashboardService.softDeleteDashboardById(dashboardID))
                .message("Dashboard soft deleted successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/restore/{dashboardID}")
    @Operation(summary = "Restore soft-deleted dashboard by ID")
    ApiResponse<Dashboard> restoreDashboardByID(@PathVariable String dashboardID) {
        return ApiResponse.<Dashboard>builder()
                .data(dashboardService.restoreDashboardById(dashboardID))
                .message("Dashboard restored successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/{dashboardID}")
    @Operation(summary = "Update dashboard by ID")
    ApiResponse<Dashboard> updateDashboardByID(@PathVariable String dashboardID,
                                               @RequestBody DashboardUpdateRequest request) {
        return ApiResponse.<Dashboard>builder()
                .data(dashboardService.updateDashboard(dashboardID, request))
                .message("Dashboard updated successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PostMapping("grant-access/group/{groupID}/dashboard/{dashboardID}")
    @Operation(summary = "Grant dashboard access to group")
    public ApiResponse<GroupDashboardAccess> grantDashboardAccessToGroup(@PathVariable String groupID,
                                                                         @PathVariable String dashboardID) {
        GroupDashboardAccess gda = groupDashboardAccessService.addDashboardToGroup(groupID,dashboardID);
        return ApiResponse.<GroupDashboardAccess>builder()
                .data(gda)
                .message("Dashboard access granted to group successfully")
                .code(1000)
                .build();

    }
    @GetMapping("/group-access/group/{groupID}")
    @Operation(summary = "Get dashboards accessible by group")
    public ApiResponse<List<Dashboard>> getDashboardsByGroupAccess(@PathVariable String groupID) {
        List<Dashboard> dashboards = groupDashboardAccessService.getDashboardsByGroupID(groupID);
        return ApiResponse.<List<Dashboard>>builder()
                .data(dashboards)
                .message("Dashboards fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/group-access/dashboard/{dashboardID}")
    @Operation(summary = "Get groups having access to a dashboard")
    public ApiResponse<List<Group>> getGroupsByDashboardAccess(@PathVariable String dashboardID){
        List<Group> groups = groupDashboardAccessService.getGroupsByDashboardID(dashboardID);
        return ApiResponse.<List<Group>>builder()
                .data(groups)
                .message("Groups fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/revoke-access/group/{groupID}/dashboard/{dashboardID}")
    @Operation(summary = "Revoke dashboard access from group")
    public ApiResponse<GroupDashboardAccess> revokeDashboardAccessFromGroup(@PathVariable String groupID,
                                                                            @PathVariable String dashboardID) {
        GroupDashboardAccess gda = groupDashboardAccessService.getGroupDashboardAccessByGroupAndDashboard(groupID, dashboardID);
        groupDashboardAccessService.updateGroupDashboardAccessStatus(gda.getId(), GroupDashboardStatus.ACTIVE);
        return ApiResponse.<GroupDashboardAccess>builder()
                .data(gda)
                .message("Dashboard access revoked from group successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('BI')")
    @PutMapping("/restore-access/group/{groupID}/dashboard/{dashboardID}")
    @Operation(summary = "Restore dashboard access to group")
    public ApiResponse<GroupDashboardAccess> restoreDashboardAccessToGroup(@PathVariable String groupID,
                                                                           @PathVariable String dashboardID) {
        GroupDashboardAccess gda = groupDashboardAccessService.getGroupDashboardAccessByGroupAndDashboard(groupID, dashboardID);
        groupDashboardAccessService.updateGroupDashboardAccessStatus(gda.getId(), GroupDashboardStatus.INACTIVE);
        return ApiResponse.<GroupDashboardAccess>builder()
                .data(gda)
                .message("Dashboard access restored to group successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/group-access/group/{groupID}/dashboard/{dashboardID}")
    @Operation(summary = "Get group-dashboard access ")
    public ApiResponse<GroupDashboardAccess> getGroupDashboardAccessDetails(@PathVariable String groupID,
                                                                          @PathVariable String dashboardID) {
        GroupDashboardAccess gda = groupDashboardAccessService.getGroupDashboardAccessByGroupAndDashboard(groupID, dashboardID);
        return ApiResponse.<GroupDashboardAccess>builder()
                .data(gda)
                .message("Group-dashboard access details fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/check-access/{dashboardID}")
    @Operation(summary = "Get dashboard if logged-in user has access to a dashboard")
    public ApiResponse<Dashboard> checkDashboardAccessForLoggedInUser(@PathVariable String dashboardID) {
        Dashboard dashboard = dashboardService.getDashboardByIdAndCheckAccess(dashboardID);
        return ApiResponse.<Dashboard>builder()
                .data(dashboard)
                .message("Dashboard access check completed successfully")
                .code(1000)
                .build();
    }
}
