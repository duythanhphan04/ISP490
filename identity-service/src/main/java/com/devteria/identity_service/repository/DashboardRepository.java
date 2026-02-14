package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.DashboardCategory;
import com.devteria.identity_service.enums.DashboardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, String> {
    @Query("SELECT d FROM Dashboard d WHERE d.dashboard_name = :dashboardName ")
    Dashboard findByName(@Param("dashboardName") String dashboardName);

    Dashboard findByCategory(DashboardCategory category);

    List<Dashboard> findAllByCreatedBy(User user);

    List<Dashboard> findAllByCategory(DashboardCategory category);

    List<Dashboard> findAllByStatus(DashboardStatus status);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Dashboard d WHERE d.dashboard_name = :name")
    boolean existsByDashboardName(@Param("name") String dashboardName);
}
