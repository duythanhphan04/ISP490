package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.GroupCreationRequest;
import com.devteria.identity_service.dto.request.GroupUpdateRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.entity.UserGroup;
import com.devteria.identity_service.enums.*;
import com.devteria.identity_service.service.GroupService;
import com.devteria.identity_service.service.SystemAuditLogService;
import com.devteria.identity_service.service.UserGroupService;
import com.devteria.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    GroupService groupService;
    @Autowired
    SystemAuditLogService systemAuditLogService;
    @Autowired
    UserService userService;
    @Autowired
    UserGroupService userGroupService;
    @PostMapping
    @Operation(summary = "Create a new group")
    public ApiResponse<Group> createGroup(@RequestBody @Valid GroupCreationRequest groupCreationRequest) {
        Group group = groupService.createGroup(groupCreationRequest);
        return ApiResponse.<Group>builder()
                .data(group)
                .message("Group created successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/{groupID}")
    @Operation(summary = "Get group by ID")
    public ApiResponse<Group> getGroupByID(@PathVariable String groupID) {
        Group group = groupService.getGroupByID(groupID);
        return ApiResponse.<Group>builder()
                .data(group)
                .message("Group fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping
    @Operation(summary = "Get all groups")
    public ApiResponse<List<Group>> getAllGroups() {
        List<Group> groups = groupService.findAllGroup();
        return ApiResponse.<List<Group>>builder()
                .data(groups)
                .message("Groups fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/type/{type}")
    @Operation(summary = "Get groups by type")
    public ApiResponse<List<Group>> getGroupsByType(@PathVariable GroupType type) {
        List<Group> groups = groupService.getGroupByType(type);
        return ApiResponse.<List<Group>>builder()
                .data(groups)
                .message("Groups fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/status/{status}")
    @Operation(summary = "Get groups by status")
    public ApiResponse<List<Group>> getGroupsByStatus(@PathVariable GroupStatus status) {
        List<Group> groups = groupService.getGroupsByStatus(status);
        return ApiResponse.<List<Group>>builder()
                .data(groups)
                .message("Groups fetched successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/soft-delete/{groupID}")
    @Operation(summary = "Soft delete group by ID")
    public ApiResponse<Group> softDeleteGroup(@PathVariable String groupID) {
        Group group = groupService.setGroupStatus(groupID, GroupStatus.INACTIVE);
        return ApiResponse.<Group>builder()
                .data(group)
                .message("Group soft deleted successfully")
                .code(1000)
                .build();
    }
    @PutMapping("delete/{groupID}")
    @Operation(summary = "Delete group by ID")
    public ApiResponse<Group> deleteGroup(@PathVariable String groupID) {
        Group group = groupService.deleteGroup(groupID);
        return ApiResponse.<Group>builder()
                .data(group)
                .message("Group deleted successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/{groupID}")
    @Operation(summary = "Update group by ID")
    public ApiResponse<Group> updateGroup(
            @PathVariable String groupID, @RequestBody @Valid GroupUpdateRequest groupUpdateRequest) {
        return ApiResponse.<Group>builder()
                .data(groupService.updateGroup(groupID, groupUpdateRequest))
                .message("Group updated successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/members/{groupID}")
    @Operation(summary = "Get group members by group ID")
    public ApiResponse<Integer> getGroupMembers(@PathVariable String groupID) {
        Integer members = groupService.getMemberCount(groupID);
        return ApiResponse.<Integer>builder()
                .data(members)
                .message("Group members fetched successfully")
                .code(1000)
                .build();
    }
    @PostMapping("/add-member/{groupID}/user/{userID}")
    @Operation(summary = "Add member to group")
    public ApiResponse<UserGroup> addMemberToGroup(@PathVariable String groupID, @PathVariable String userID) {
        UserGroup userGroup = userGroupService.addUserToGroup(userID,groupID);
        return ApiResponse.<UserGroup>builder()
                .data(userGroup)
                .message("User added to group successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/user-by-group/{groupID}")
    @Operation(summary = "Get user groups by group ID")
    public ApiResponse<List<User>> getUserGroupsByGroupID(@PathVariable String groupID) {
        List<User> userGroups = userGroupService.getUserByGroupID(groupID);
        return ApiResponse.<List<User>>builder()
                .data(userGroups)
                .message("User groups fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/groups-by-user/{userID}")
    @Operation(summary = "Get groups by user ID")
    public ApiResponse<List<Group>> getGroupsByUserID(@PathVariable String userID) {
        List<Group> groups = userGroupService.getGroupsByUserID(userID);
        return ApiResponse.<List<Group>>builder()
                .data(groups)
                .message("Groups fetched successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/soft-remove-member/{groupID}/user/{userID}")
    @Operation(summary = "Soft remove member from group")
    public ApiResponse<UserGroup> removeMemberFromGroup(@PathVariable String groupID, @PathVariable String userID) {
        UserGroup userGroup = userGroupService.getUserGroupByUserIDAndGroupID(userID,groupID);
        userGroupService.setUserGroupStatus(userGroup.getId(),GroupMemberStatus.ACTIVE);
        return ApiResponse.<UserGroup>builder()
                .data(userGroup)
                .message("User removed from group successfully")
                .code(1000)
                .build();
    }
    @PutMapping("/restore-member/{groupID}/user/{userID}")
    @Operation(summary = "Restore member to group")
    public ApiResponse<UserGroup> restoreMemberToGroup(@PathVariable String groupID, @PathVariable String userID) {
        UserGroup userGroup = userGroupService.getUserGroupByUserIDAndGroupID(userID, groupID);
        userGroupService.setUserGroupStatus(userGroup.getId(), GroupMemberStatus.INACTIVE);
        return ApiResponse.<UserGroup>builder()
                .data(userGroup)
                .message("User restored to group successfully")
                .code(1000)
                .build();
    }
}
