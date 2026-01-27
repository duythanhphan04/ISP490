package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.enums.GroupType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupCreationRequest {
    @NotBlank(message = "Group name is required")
    @Size(max = 255, message = "Group name must be less than 256 characters")
    String group_name;
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be less than 256 characters")
    String description;
    GroupType group_type;
}
