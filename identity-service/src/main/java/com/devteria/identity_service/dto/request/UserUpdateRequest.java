package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.entity.Department;
import com.devteria.identity_service.enums.SystemRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String user_name;
    String email;
    SystemRole role;
    Department department;
}
