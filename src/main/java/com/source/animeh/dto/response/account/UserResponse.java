package com.source.animeh.dto.response.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

  String id;
  String username;
  String password;
  String email;
  String role;
  String displayName;
  String fullName;
  String avatarUrl;
  String backgroundUrl;
  Boolean gender;
  String bio;
  String cccd;
  String phoneNumber;
  Boolean isActive;
  LocalDateTime lastLogin;
  BigDecimal gpValue;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  String moderatorRequestStatus;
}
