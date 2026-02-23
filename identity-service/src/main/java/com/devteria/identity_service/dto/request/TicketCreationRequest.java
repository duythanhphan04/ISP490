package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.enums.RequestType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketCreationRequest {
    RequestType type;
    String description;
}
