package com.source.animeh.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdatePasswordRequest {

  @NotBlank(message = "REQUIRED_PASSWORD")
  String oldPassword;

  @NotBlank(message = "REQUIRED_PASSWORD")
  @Size(min = 8, message = "INVALID_PASSWORD")
  String newPassword;
}
