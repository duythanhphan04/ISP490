package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Dashboard;
import com.devteria.identity_service.entity.Group;
import com.devteria.identity_service.entity.GroupDashboardAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupDashboardAccessRepository extends JpaRepository<GroupDashboardAccess,String> {

    boolean existsByGroupAndDashboard(Group group, Dashboard dashboard);

    @Query("SELECT gda.dashboard FROM GroupDashboardAccess gda " +
            "WHERE gda.group.group_id = :groupId AND gda.status = 'INACTIVE'")
    List<Dashboard> findAllDashboardsByGroupId(@Param("groupId") String groupId);

    @Query(" SELECT gda.group FROM GroupDashboardAccess gda " +
            "WHERE gda.dashboard.dashboard_id = :dashboardId AND gda.status = 'INACTIVE'")
    List<Group> findAllGroupsByDashboardId(String dashboardId);

    @Query("SELECT gda FROM GroupDashboardAccess gda " +
            "WHERE gda.group.group_id = :groupId AND gda.dashboard.dashboard_id = :dashboardId")
    Optional<GroupDashboardAccess> findByGroupAndDashboard ( @Param("groupId") String groupId, @Param("dashboardId") String dashboardId);
}
