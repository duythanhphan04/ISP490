package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.enums.DepartmentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentCreationRequest {
    String department_name;
    String managerId;
    DepartmentType department_type;
}
