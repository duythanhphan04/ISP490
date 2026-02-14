package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.SystemAuditLogsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAuditLogDetailRepository extends JpaRepository<SystemAuditLogsDetail, String> {

}
