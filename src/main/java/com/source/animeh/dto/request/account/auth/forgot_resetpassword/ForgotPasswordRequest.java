package com.source.animeh.dto.request.account.auth.forgot_resetpassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {

  @NotBlank(message = "REQUIRED_EMAIL")
  @Email(message = "INVALID_EMAIL")
  String email;
}
