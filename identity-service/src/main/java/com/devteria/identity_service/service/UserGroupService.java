package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.entity.UserGroup;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.GroupMemberStatus;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.GroupRepository;
import com.devteria.identity_service.repository.UserGroupRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserGroupService {
    GroupService groupService;
    UserGroupRepository userGroupRepository;
    GroupRepository groupRepository;
    UserService userService;
    SystemAuditLogService systemAuditLogService;
    @Transactional
    public UserGroup addUserToGroup(String userId, String groupId) {
        User user = userService.getUserByID(userId);
        Group group = groupService.getGroupByID(groupId);
        boolean exists = userGroupRepository.existsByUserAndGroup(user, group);
        if (exists) {
            throw new WebException(ErrorCode.USER_ALREADY_IN_GROUP);
        }
        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(group)
                .added_at(Instant.now())
                .status(GroupMemberStatus.INACTIVE)
                .build();
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);

        group.setMember(group.getMember() + 1);
        groupRepository.save(group);
        systemAuditLogService.logEvent(
                userService.getLoggedInUser(),
                EventLog.MEMBER_ADDED_TO_GROUP,
                TargetEntity.GROUP,
                groupId
        );

        return savedUserGroup;
    }
    public List<User> getUserByGroupID (String groupId) {
        return userGroupRepository.findAllUsersByGroupId(groupId);
    }
    public List<Group> getGroupsByUserID (String userId) {
        return userGroupRepository.findAllGroupsByUserId(userId);
    }
    @Transactional
    public UserGroup setUserGroupStatus(String userGroupId, GroupMemberStatus newStatus) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_IN_GROUP));

        GroupMemberStatus oldStatus = userGroup.getStatus();
        if (newStatus == GroupMemberStatus.ACTIVE && oldStatus == GroupMemberStatus.ACTIVE) {
            throw new WebException(ErrorCode.MEMBER_ALREADY_DELETED);
        }
        if (newStatus == GroupMemberStatus.INACTIVE && oldStatus == GroupMemberStatus.INACTIVE) {
            throw new WebException(ErrorCode.MEMBER_NOT_DELETED_YET);
        }
        userGroup.setStatus(newStatus);
        UserGroup savedUserGroup = userGroupRepository.save(userGroup);
        Group group = userGroup.getGroup();
        if (newStatus == GroupMemberStatus.ACTIVE) {
            if (group.getMember() > 0) {
                group.setMember(group.getMember() - 1);
            }
            systemAuditLogService.logEvent(
                    userService.getLoggedInUser(),
                    EventLog.MEMBER_SOFT_DELETED,
                    TargetEntity.GROUP,
                    group.getGroup_id()
            );

        } else {
            group.setMember(group.getMember() + 1);
            systemAuditLogService.logEvent(
                    userService.getLoggedInUser(),
                    EventLog.MEMBER_RESTORED,
                    TargetEntity.GROUP,
                    group.getGroup_id()
            );
        }
        groupRepository.save(group);
        return savedUserGroup;
    }
    public UserGroup getUserGroupByUserIDAndGroupID(String userId, String groupId) {
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (optionalUserGroup.isPresent()) {
            return optionalUserGroup.get();
        } else {
            throw new WebException(ErrorCode.USER_NOT_IN_GROUP);
        }
    }
}
