package com.source.animeh.dto.response.account;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModeratorRequest_Response {

  String id;
  String userId;
  String userUsername;
  String adminId;
  String adminUsername;
  LocalDateTime requestDate;
  String status;
  String commentRejected;
  LocalDateTime processedAt;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
