package com.devteria.identity_service.service;

import com.devteria.identity_service.annotation.AuditableField;
import com.devteria.identity_service.entity.SystemAuditLogs;
import com.devteria.identity_service.entity.SystemAuditLogsDetail;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.repository.SystemAuditLogDetailRepository;
import com.devteria.identity_service.repository.SystemAuditLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemAuditLogService {
    private static final Log log = LogFactory.getLog(SystemAuditLogService.class);
    SystemAuditLogRepository systemAuditLogRepository;
    SystemAuditLogDetailRepository systemAuditLogDetailRepository;

    public void logEvent(User user, EventLog eventLog, TargetEntity targetEntity, String targetEntityId) {
        SystemAuditLogs log = new SystemAuditLogs();
        log.setUser(user);
        log.setAction(eventLog);
        log.setTarget_entity(targetEntity);
        log.setTarget_id(targetEntityId);
        log.setCreated_at(Instant.now());
        systemAuditLogRepository.save(log);
    }
    @Transactional
    public <T> void logEntityUpdate(User user, T oldEntity, T newEntity, String targetId, TargetEntity targetEntity, EventLog eventLog) {
        List<SystemAuditLogsDetail> details = new ArrayList<>();
        Field[] fields = oldEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(AuditableField.class)){
                field.setAccessible(true);
                try{
                    Object oldValue =field.get(oldEntity);
                    Object newValue =field.get(newEntity);
                    if(!Objects.equals(oldValue,newValue)){
                        String displayName = field.getAnnotation(AuditableField.class).value();
                        details.add(SystemAuditLogsDetail.builder()
                                        .column_name(displayName)
                                        .old_value(formatValue(oldValue))
                                        .new_value(formatValue(newValue))
                                .build());
                    }
                } catch (IllegalAccessException e) {
                    log.error("Audit Log Error: Cannot access field {}");
                }
            }
        }
        if(!details.isEmpty()) {
            SystemAuditLogs parentLog = SystemAuditLogs.builder()
                    .user(user)
                    .action(eventLog)
                    .target_entity(targetEntity)
                    .target_id(targetId)
                    .created_at(Instant.now())
                    .build();
            SystemAuditLogs savedLog = systemAuditLogRepository.save(parentLog);
            details.forEach(detail -> detail.setSystemAuditLogs(savedLog));
            systemAuditLogDetailRepository.saveAll(details);
        }

    }
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Instant) {
            return ((Instant) value).toString();
        }
        return value.toString();
    }
}