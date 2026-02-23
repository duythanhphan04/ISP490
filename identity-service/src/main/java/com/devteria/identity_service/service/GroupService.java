package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.GroupCreationRequest;
import com.devteria.identity_service.dto.request.GroupUpdateRequest;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.GroupStatus;
import com.devteria.identity_service.enums.GroupType;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private static final Log log = LogFactory.getLog(GroupService.class);
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SystemAuditLogService systemAuditLogService;
    @Autowired
    UserService userService;
    public List<Group> findAllGroup() {
        return groupRepository.findAll();
    }
    public Group getGroupByID(String groupID) {
        return groupRepository.findById(groupID).orElseThrow( () -> new WebException(ErrorCode.GROUP_NOT_FOUND));
    }
    @Transactional
    public Group createGroup(GroupCreationRequest request) {
        User loggedInUser = userService.getLoggedInUser();
        Group group = Group.builder()
                .group_name(request.getGroup_name())
                .description(request.getDescription())
                .member(0)
                .status(GroupStatus.ACTIVE)
                .createdAt(Instant.now())
                .groupType(request.getGroup_type())
                .build();
        groupRepository.save(group);
        systemAuditLogService.logEvent(
                loggedInUser,
                EventLog.GROUP_CREATED,
                TargetEntity.GROUP,
                group.getGroup_id()
        );
        return group;
    }
    @Transactional
    public Group deleteGroup(String groupID) {
        Group group = getGroupByID(groupID);
        if (group.getMember() > 0) {
            throw new WebException(ErrorCode.GROUP_CANNOT_DELETE_HAS_MEMBERS);
        }
        groupRepository.delete(group);
        systemAuditLogService.logEvent(
                userService.getLoggedInUser(),
                EventLog.GROUP_DELETED,
                TargetEntity.GROUP,
                groupID
        );
        return group;
    }
    public Group updateGroup(String groupID, GroupUpdateRequest groupUpdateRequest) {
        Group group = getGroupByID(groupID);
        Group oldGroupSnapshot = new Group();
        BeanUtils.copyProperties(group, oldGroupSnapshot);
        group.setGroup_name(groupUpdateRequest.getGroup_name());
        group.setDescription(groupUpdateRequest.getDescription());
        group.setGroupType(groupUpdateRequest.getGroup_type());
        Group updatedGroup = groupRepository.save(group);
        try{
            systemAuditLogService.logEntityUpdate(
                    userService.getLoggedInUser(),
                    oldGroupSnapshot,
                    updatedGroup,
                    groupID,
                    TargetEntity.GROUP,
                    EventLog.GROUP_UPDATED
            );
        }catch (Exception e){
            log.error("Failed to log group update: " + e.getMessage());
        }
        return updatedGroup;
    }
    public int getMemberCount(String groupID) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            return group.getMember();
        }
        return 0;
    }
    public Group updateGroupStatus(String groupID, GroupStatus status) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            group.setStatus(status);
            return groupRepository.save(group);
        }
        return null;
    }
    public List<Group> getGroupsByStatus(GroupStatus status) {
        return groupRepository.findByStatus(status);
    }
    public List<Group> getGroupByType(GroupType group_type) {
        return groupRepository.findByGroupType(group_type);
    }
    @Transactional
    public Group setGroupStatus(String groupID, GroupStatus status) {
        Group group = getGroupByID(groupID);
        if (group.getStatus() == status) {
            return group;
        }
        group.setStatus(status);
        Group updatedGroup = groupRepository.save(group);

        EventLog eventLog = (status == GroupStatus.INACTIVE)
                ? EventLog.GROUP_SOFT_DELETED
                : EventLog.GROUP_RESTORED;

        // 5. Ghi Log
        systemAuditLogService.logEvent(
                userService.getLoggedInUser(),
                eventLog,
                TargetEntity.GROUP,
                groupID
        );

        return updatedGroup;
    }
}
