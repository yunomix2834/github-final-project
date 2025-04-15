package com.source.animeh.dto.response.account;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModeratorProfileResponse {

  String username;
  String fullName;
  String cccd;
  String phoneNumber;
}
