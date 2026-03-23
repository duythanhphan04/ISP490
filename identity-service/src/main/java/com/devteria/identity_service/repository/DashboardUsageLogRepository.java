package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.DashboardUsageLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardUsageLogRepository extends JpaRepository<DashboardUsageLogs, String> {
}
