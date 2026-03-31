package com.devteria.identity_service.controller;
import com.devteria.identity_service.dto.request.DepartmentCreationRequest;
import com.devteria.identity_service.dto.request.DepartmentUpdateRequest;
import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.enums.DepartmentStatus;
import com.devteria.identity_service.service.DepartmentService;
import com.devteria.identity_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Builder
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    UserService userService;
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    @Operation(summary = "Create a new department")
    public ApiResponse<Department> createDepartment(@RequestBody DepartmentCreationRequest request) {
        Department department = departmentService.createDepartment(request);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department created successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('BI')")
    @GetMapping
    @Operation(summary = "Get all department")
    public ApiResponse<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ApiResponse.<List<Department>>builder()
                .data(departments)
                .message("Departments fetched successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/{departmentID}")
    @Operation(summary = "Get department by ID")
    public ApiResponse<Department> getDepartmentByID(@PathVariable String departmentID) {
        Department department = departmentService.getDepartmentByID(departmentID);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department fetched successfully")
                .code(1000)
                .build();
    }
    @GetMapping("/status/{status}")
    @Operation(summary = "Get departments by status")
    public ApiResponse<List<Department>> getAllDepartmentsByStatus(@PathVariable DepartmentStatus status) {
        List<Department> departments = departmentService.getDepartmentsByStatus(status);
        return ApiResponse.<List<Department>>builder()
                .data(departments)
                .message("Departments fetched successfully")
                .code(1000)
                .build();
    }

    @GetMapping("/manager/{managerID}")
    @Operation(summary = "Get departments by manager ID")
    public ApiResponse<List<Department>> getDepartmentsByManagerID(@PathVariable String managerID){
        List<Department> departments = departmentService.getDepartmentByManagerID(managerID);
        return ApiResponse.<List<Department>>builder()
                .data(departments)
                .message("Departments fetched successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping
    @Operation(summary = "Delete department by ID")
    public ApiResponse<Department> deleteDepartmentByID(@RequestParam String departmentID) {
        Department department = departmentService.deleteDepartmentByID(departmentID);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department deleted successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/soft-delete/{departmentID}")
    @Operation(summary = "Soft delete department by ID")
    public ApiResponse<Department> softDeleteDepartmentByID(@PathVariable String departmentID) {
        Department department = departmentService.softDeleteDepartmentByID(departmentID);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department soft deleted successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/restore/{departmentID}")
    @Operation(summary = "Restore department by ID")
    public ApiResponse<Department> restoreDepartmentByID(@PathVariable String departmentID) {
        Department department = departmentService.restoreDepartmentByID(departmentID);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department restored successfully")
                .code(1000)
                .build();
    }
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{departmentID}")
    @Operation(summary = "Update department by ID")
    public ApiResponse<Department> updateDepartment(
            @PathVariable String departmentID, @RequestBody DepartmentUpdateRequest request) {
        Department department = departmentService.updateDepartment(departmentID, request);
        return ApiResponse.<Department>builder()
                .data(department)
                .message("Department updated successfully")
                .code(1000)
                .build();
    }
}
