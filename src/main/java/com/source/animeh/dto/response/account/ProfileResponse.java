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
public class ProfileResponse {

  String username;
  String email;
  String role;

  String avatarUrl;
  String backgroundUrl;

  String displayName;
  String fullName;
  Boolean gender;
  String bio;
  String cccd;
  String phoneNumber;
  Boolean isActive;
  LocalDateTime lastLogin;
  BigDecimal gpValue;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
