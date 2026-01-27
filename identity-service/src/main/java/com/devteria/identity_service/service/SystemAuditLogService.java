package com.devteria.identity_service.service;

import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.SystemAuditLogs;
import com.devteria.identity_service.entity.SystemAuditLogsDetail;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.repository.httpclient.SystemAuditLogDetailRepository;
import com.devteria.identity_service.repository.httpclient.SystemAuditLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemAuditLogService {
    SystemAuditLogRepository systemAuditLogRepository;
    SystemAuditLogDetailRepository systemAuditLogDetailRepository;

    public SystemAuditLogs logEvent(User user, EventLog eventLog, TargetEntity targetEntity, String targetEntityId) {
        SystemAuditLogs log = new SystemAuditLogs();
        log.setUser(user);
        log.setAction(eventLog);
        log.setTarget_entity(targetEntity);
        log.setTarget_id(targetEntityId);
        log.setCreated_at(Instant.now());
        systemAuditLogRepository.save(log);
        return log;
    }

    @Transactional
    public void logGroupUpdate(User user, Group oldGroup, Group newGroup) {
        List<SystemAuditLogsDetail> details = new ArrayList<>();
        if (!Objects.equals(oldGroup.getGroup_name(), newGroup.getGroup_name())) {
            details.add(createRawDetail("group_name", oldGroup.getGroup_name(), newGroup.getGroup_name()));
        }
        if (!Objects.equals(oldGroup.getDescription(), newGroup.getDescription())) {
            details.add(createRawDetail("description", oldGroup.getDescription(), newGroup.getDescription()));
        }
        if (!Objects.equals(oldGroup.getGroupType(), newGroup.getGroupType())) {
            details.add(createRawDetail("group_type",
                    String.valueOf(oldGroup.getGroupType()),
                    String.valueOf(newGroup.getGroupType())));
        }
        if (!details.isEmpty()) {
            SystemAuditLogs parentLog = logEvent(user, EventLog.GROUP_UPDATED, TargetEntity.GROUP, newGroup.getGroup_id());
            details.forEach(detail -> detail.setSystemAuditLogs(parentLog));
            systemAuditLogDetailRepository.saveAll(details);
        }
    }
    private SystemAuditLogsDetail createRawDetail(String columnName, String oldValue, String newValue) {
        return SystemAuditLogsDetail.builder()
                .column_name(columnName)
                .old_value(oldValue != null ? oldValue : "")
                .new_value(newValue != null ? newValue : "")
                .build();
    }
}