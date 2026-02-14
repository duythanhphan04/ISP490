package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.enums.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    List<Department> findByStatus(DepartmentStatus status);
    @Query("SELECT d FROM Department d WHERE d.manager.user_id = :managerID")
    List<Department> findByManagerID( @Param("managerID") String managerID);
    @Query("SELECT d FROM Department d WHERE d.department_name = :departmentName")
    Department findByName( @Param("departmentName") String departmentName);
}
