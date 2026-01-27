package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.GroupCreationRequest;
import com.devteria.identity_service.dto.request.GroupUpdateRequest;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.GroupStatus;
import com.devteria.identity_service.enums.GroupType;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.httpclient.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
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
    public Group createGroup(GroupCreationRequest groupCreationRequest) {
        Group group = Group.builder()
                .group_name(groupCreationRequest.getGroup_name())
                .description(groupCreationRequest.getDescription())
                .member(0)
                .status(GroupStatus.ACTIVE)
                .createdAt(java.time.Instant.now())
                .groupType(groupCreationRequest.getGroup_type())
                .build();
        return groupRepository.save(group);
    }
    public Group deleteGroup(String groupID) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            groupRepository.deleteById(groupID);
        }
        return group;
    }
    public Group updateGroup(String groupID, GroupUpdateRequest groupUpdateRequest) {
        User user = userService.getLoggedInUser();
        Group exitingGroup = getGroupByID(groupID);
        Group oldGroup = new Group();
        oldGroup.setGroup_id(exitingGroup.getGroup_id());
        oldGroup.setGroup_name(exitingGroup.getGroup_name());
        oldGroup.setDescription(exitingGroup.getDescription());
        oldGroup.setGroupType(exitingGroup.getGroupType());
        exitingGroup.setGroup_name(groupUpdateRequest.getGroup_name());
        exitingGroup.setDescription(groupUpdateRequest.getDescription());
        exitingGroup.setGroupType(groupUpdateRequest.getGroup_type());
        Group updatedGroup = groupRepository.save(exitingGroup);
        systemAuditLogService.logGroupUpdate(user, oldGroup, updatedGroup);
        return updatedGroup;
    }
    public Group incrementMemberCount(String groupID) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            group.setMember(group.getMember() + 1);
            return groupRepository.save(group);
        }
        return null;
    }
    public  Group decreaseMemberCount(String groupID) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            group.setMember(group.getMember() - 1);
            return groupRepository.save(group);
        }
        return null;
    }
    public Group updateMemberCount(String groupID, int count) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            group.setMember(count);
            return groupRepository.save(group);
        }
        return null;
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
    public Group setGroupStatus(String groupID, GroupStatus status) {
        Group group = getGroupByID(groupID);
        if(group != null) {
            group.setStatus(status);
            return groupRepository.save(group);
        }
        return null;
    }
}
