package com.source.animeh.dto.request.account.auth.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequestRegister {

  // TODO Xác thực không cho thêm ký tự đặc biệt vào username
  @NotBlank(message = "REQUIRED_USERNAME")
  @Size(min = 4, max = 100, message = "INVALID_USERNAME")
  String username;

  @NotBlank(message = "REQUIRED_PASSWORD")
  @Size(min = 8, message = "INVALID_PASSWORD")
  String password;

  @NotBlank(message = "REQUIRED_EMAIL")
  @Email(message = "INVALID_EMAIL")
  @Size(min = 6, max = 100, message = "INVALID_EMAIL")
  String email;
}
