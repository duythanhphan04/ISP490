package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.DashboardCategory;
import com.devteria.identity_service.enums.DepartmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "department_id", nullable = false)
    String department_id;

    @Size(max = 255)
    @Column(name = "department_name", nullable = false)
    String department_name;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "manager", nullable = false)
    User manager;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    DepartmentStatus status;
}
