package com.devteria.identity_service.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OutBoundUserResponse {
  String id;
  String email;
  Boolean verified_email;
  String name;
  String given_name;
  String family_name;
  String picture;
  String locale;
}
