package com.devteria.identity_service.service;
import com.devteria.identity_service.dto.request.DepartmentCreationRequest;
import com.devteria.identity_service.dto.request.DepartmentUpdateRequest;
import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.DepartmentStatus;
import com.devteria.identity_service.enums.EventLog;
import com.devteria.identity_service.enums.TargetEntity;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.DepartmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class DepartmentService {
    DepartmentRepository departmentRepository;
    UserService userService;
    SystemAuditLogService systemAuditLogService;

    public Department createDepartment(DepartmentCreationRequest request) {
        if(getDepartmentByName(request.getDepartment_name()) != null){
            throw new WebException(ErrorCode.DEPARTMENT_ALREADY_EXISTS);
        }
        Department department = Department.builder()
                .department_name(request.getDepartment_name())
                .manager(userService.getUserByID(request.getManagerId()))
                .department_type(request.getDepartment_type())
                .status(DepartmentStatus.ACTIVE)
                .build();
        systemAuditLogService.logEvent(
                userService.getLoggedInUser(),
                EventLog.DEPARTMENT_CREATED,
                TargetEntity.DEPARTMENT,
                department.getDepartment_id()
        );
        return departmentRepository.save(department);
    }
    public Department getDepartmentByName(String departmentName) {
        return departmentRepository.findByName(departmentName);
    }
    public Department getDepartmentByID(String departmentID) {
        return departmentRepository.findById(departmentID).orElseThrow( () -> new WebException(ErrorCode.DEPARTMENT_NOT_FOUND));
    }
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    public List<Department> getDepartmentsByStatus(DepartmentStatus status) {
        return departmentRepository.findByStatus(status);
    }
    public List<Department> getDepartmentByManagerID(String managerID) {
        return departmentRepository.findByManagerID(managerID);
    }
    public Department deleteDepartmentByID(String departmentID) {
        User loggedInUser = userService.getLoggedInUser();
        Department department = getDepartmentByID(departmentID);
        departmentRepository.delete(department);
        systemAuditLogService.logEvent(loggedInUser, EventLog.DEPARTMENT_DELETED, TargetEntity.DEPARTMENT, departmentID);
        return department;
    }
    public Department softDeleteDepartmentByID(String departmentID) {
        User loggedInUser = userService.getLoggedInUser();
        Department department = getDepartmentByID(departmentID);
        department.setStatus(DepartmentStatus.INACTIVE);
        systemAuditLogService.logEvent(loggedInUser, EventLog.DEPARTMENT_SOFT_DELETED, TargetEntity.DEPARTMENT, departmentID);
        return departmentRepository.save(department);
    }
    public Department restoreDepartmentByID(String departmentID) {
        User loggedInUser = userService.getLoggedInUser();
        Department department = getDepartmentByID(departmentID);
        department.setStatus(DepartmentStatus.ACTIVE);
        systemAuditLogService.logEvent( loggedInUser, EventLog.DEPARTMENT_RESTORED, TargetEntity.DEPARTMENT, departmentID);
        return departmentRepository.save(department);
    }
    public Department setDepartmentStatus(String departmentID, DepartmentStatus status) {
        Department department = getDepartmentByID(departmentID);
        department.setStatus(status);
        return departmentRepository.save(department);
    }
    @Transactional
    public Department updateDepartment(String departmentID, DepartmentUpdateRequest request){
        Department department = getDepartmentByID(departmentID);
        Department oldDepartmentSnapshot = Department.builder()
                .department_name(department.getDepartment_name())
                .manager(department.getManager())
                .department_type(department.getDepartment_type())
                .status(department.getStatus())
                .build();
        if(request.getDepartment_name() != null){
            department.setDepartment_name(request.getDepartment_name());
        }
        if(request.getManagerId() != null){
            User newManager = userService.getUserByID(request.getManagerId());
            department.setManager(newManager);
        }
        Department updatedDepartment = departmentRepository.save(department);
        systemAuditLogService.logEntityUpdate(
                userService.getLoggedInUser(),
                oldDepartmentSnapshot,
                updatedDepartment,
                departmentID,
                TargetEntity.DEPARTMENT,
                EventLog.DEPARTMENT_UPDATED
        );
        return updatedDepartment;
    }
}
