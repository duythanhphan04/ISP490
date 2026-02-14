package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.enums.DashboardCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardUpdateRequest {
    String dashboardName;
    String urlPath;
    DashboardCategory dashboardCategory;
}
