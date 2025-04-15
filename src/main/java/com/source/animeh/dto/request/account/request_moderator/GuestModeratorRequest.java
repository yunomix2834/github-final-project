package com.source.animeh.dto.request.account.request_moderator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GuestModeratorRequest {

  String username;
  String password;
  String email;
  String fullName;
  String cccd;
  String phoneNumber;
}
