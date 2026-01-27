package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.entity.Department;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String user_name;
    String email;
    Department department;
}
