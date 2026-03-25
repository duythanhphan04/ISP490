package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.DashboardUsageLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardUsageLogRepository extends JpaRepository<DashboardUsageLogs, String> {
    @Query("SELECT l FROM DashboardUsageLogs l WHERE l.dashboard.dashboard_id = :dashboardId")
    List<DashboardUsageLogs> findAllByDashboardId( @Param("dashboardId") String dashboardId);
    @Query("SELECT l FROM DashboardUsageLogs l WHERE l.user.user_id = :userId")
    List<DashboardUsageLogs> findAllByUserId(@Param("userId") String userId);
}
