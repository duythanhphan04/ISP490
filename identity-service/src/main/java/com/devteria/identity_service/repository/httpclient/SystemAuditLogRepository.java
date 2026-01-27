package com.devteria.identity_service.repository.httpclient;

import com.devteria.identity_service.entity.SystemAuditLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLogs, String> {

}
