package com.source.animeh.dto.response.account.auth;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

  String username;
  String role;
  String email;
  String avatarUrl;

  String tokenId;
  String tokenAccessType;
  String accessToken;
  String refreshValue;
  LocalDateTime expiryTime;
  Boolean isAuthenticated;

}
