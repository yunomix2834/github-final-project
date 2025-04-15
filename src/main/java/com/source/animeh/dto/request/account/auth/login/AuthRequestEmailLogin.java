package com.source.animeh.dto.request.account.auth.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequestEmailLogin {

  @NotBlank(message = "REQUIRED_EMAIL")
  private String email;

  @NotBlank(message = "REQUIRED_PASSWORD")
  private String password;
}
