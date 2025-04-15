package com.source.animeh.interface_package.service.account.auth;

import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ForgotPasswordRequest;
import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ResetPasswordRequest;
import com.source.animeh.dto.request.account.auth.login.AuthRequestEmailLogin;
import com.source.animeh.dto.request.account.auth.login.AuthRequestUsernameLogin;
import com.source.animeh.dto.request.account.auth.register.AuthRequestRegister;
import com.source.animeh.dto.response.account.auth.AuthResponse;

public interface AuthServiceInterface {

  // Đăng ký
  AuthResponse register(AuthRequestRegister request);

  // Login By Username
  AuthResponse loginByUsername(AuthRequestUsernameLogin request);

  // Login By Email
  AuthResponse loginByEmail(AuthRequestEmailLogin request);

  /**
   * Refresh token
   */
  AuthResponse refreshToken(String refreshValue);

  /**
   * Logout
   */
  AuthResponse logout(String refreshValue);

  /**
   * Check Access Token còn hợp lệ hay không
   */
  AuthResponse checkToken(String bearer);

  // Forgot Password
  void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  // Reset Password
  void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
