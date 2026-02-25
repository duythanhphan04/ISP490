package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.SystemAuditLogsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemAuditLogDetailRepository extends JpaRepository<SystemAuditLogsDetail, String> {

    @Query("SELECT d FROM SystemAuditLogsDetail d WHERE d.systemAuditLogs.log_id = :logId")
    List<SystemAuditLogsDetail> findByLogID(@Param("logId") String logId);
}
