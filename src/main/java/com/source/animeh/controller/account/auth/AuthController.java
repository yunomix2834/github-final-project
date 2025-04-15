package com.source.animeh.controller.account.auth;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ForgotPasswordRequest;
import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ResetPasswordRequest;
import com.source.animeh.dto.request.account.auth.login.AuthRequestEmailLogin;
import com.source.animeh.dto.request.account.auth.login.AuthRequestUsernameLogin;
import com.source.animeh.dto.request.account.auth.register.AuthRequestRegister;
import com.source.animeh.dto.response.account.auth.AuthResponse;
import com.source.animeh.service.account.auth.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu xác thực người dùng.
 * <p>
 * Các endpoint được định nghĩa dưới dạng hằng số để dễ quản lý và thay đổi sau này.
 * </p>
 *
 * @author
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class AuthController {

  // Định nghĩa các endpoint dưới dạng hằng số
  private static final String ENDPOINT_REGISTER = "/auth/register";
  private static final String ENDPOINT_LOGIN_USERNAME = "/auth/login-username";
  private static final String ENDPOINT_LOGIN_EMAIL = "/auth/login-email";
  private static final String ENDPOINT_FORGOT_PASSWORD = "/auth/forgot-password";
  private static final String ENDPOINT_RESET_PASSWORD = "/auth/reset-password";
  private static final String ENDPOINT_REFRESH_TOKEN = "/refresh-token";
  private static final String ENDPOINT_LOGOUT = "/user-logout";
  private static final String ENDPOINT_CHECK_TOKEN = "/check-token";
  // Định nghĩa các thông báo phản hồi dưới dạng hằng số
  private static final String MSG_REGISTER_SUCCESS = "Register successfully";
  private static final String MSG_LOGIN_SUCCESS = "Login successfully";
  private static final String MSG_FORGOT_PASSWORD_SUCCESS = "Send request forgot password successfully!";
  private static final String MSG_RESET_PASSWORD_SUCCESS = "Reset password successfully!";
  private static final String MSG_REFRESH_TOKEN_SUCCESS = "Refresh successfully";
  private static final String MSG_LOGOUT_SUCCESS = "Logged out!";
  private static final String MSG_CHECK_TOKEN = "Check token result";
  AuthService authService;

  /**
   * Đăng ký tài khoản.
   *
   * @param request thông tin đăng ký
   * @return ApiResponse chứa thông tin xác thực của người dùng
   */
  @PostMapping(ENDPOINT_REGISTER)
  ApiResponse<AuthResponse> register(@RequestBody @Valid AuthRequestRegister request) {
    AuthResponse authResponse = authService.register(request);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_REGISTER_SUCCESS)
        .result(authResponse)
        .build();
  }

  /**
   * Đăng nhập bằng tên người dùng.
   *
   * @param request thông tin đăng nhập sử dụng username
   * @return ApiResponse chứa thông tin xác thực của người dùng
   */
  @PostMapping(ENDPOINT_LOGIN_USERNAME)
  ApiResponse<AuthResponse> loginByUsername(
      @RequestBody @Valid AuthRequestUsernameLogin request) {
    AuthResponse authResponse = authService.loginByUsername(request);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_LOGIN_SUCCESS)
        .result(authResponse)
        .build();
  }

  /**
   * Đăng nhập bằng email.
   *
   * @param request thông tin đăng nhập sử dụng email
   * @return ApiResponse chứa thông tin xác thực của người dùng
   */
  @PostMapping(ENDPOINT_LOGIN_EMAIL)
  ApiResponse<AuthResponse> loginByEmail(@RequestBody @Valid AuthRequestEmailLogin request) {
    AuthResponse authResponse = authService.loginByEmail(request);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_LOGIN_SUCCESS)
        .result(authResponse)
        .build();
  }

  /**
   * Gửi yêu cầu quên mật khẩu.
   *
   * @param forgotPasswordRequest thông tin yêu cầu quên mật khẩu
   * @return ApiResponse thông báo quá trình gửi yêu cầu thành công
   */
  @PostMapping(ENDPOINT_FORGOT_PASSWORD)
  ApiResponse<Void> forgotPassword(
      @RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
    authService.forgotPassword(forgotPasswordRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_FORGOT_PASSWORD_SUCCESS)
        .build();
  }

  /**
   * Yêu cầu đặt lại mật khẩu.
   *
   * @param resetPasswordRequest thông tin đặt lại mật khẩu
   * @return ApiResponse thông báo quá trình đặt lại mật khẩu thành công
   */
  @PostMapping(ENDPOINT_RESET_PASSWORD)
  ApiResponse<Void> resetPassword(
      @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
    authService.resetPassword(resetPasswordRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_RESET_PASSWORD_SUCCESS)
        .build();
  }

  /**
   * Lấy lại token mới.
   *
   * @param body chứa giá trị refresh token
   * @return ApiResponse chứa thông tin xác thực của người dùng sau khi làm mới token
   */
  @PostMapping(ENDPOINT_REFRESH_TOKEN)
  ApiResponse<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
    String refreshValue = body.get("refreshValue");
    AuthResponse response = authService.refreshToken(refreshValue);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_REFRESH_TOKEN_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Đăng xuất khỏi hệ thống, xoá cookie và thu hồi refresh token.
   *
   * @param body chứa giá trị refresh token
   * @return ApiResponse chứa thông tin xác thực của người dùng sau khi đăng xuất
   */
  @PostMapping(ENDPOINT_LOGOUT)
  ApiResponse<AuthResponse> logout(@RequestBody Map<String, String> body) {
    String refreshValue = body.get("refreshValue");
    AuthResponse response = authService.logout(refreshValue);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_LOGOUT_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Kiểm tra tính hợp lệ của Access Token.
   *
   * @param bearer header Authorization chứa token
   * @return ApiResponse chứa thông tin xác thực của người dùng nếu token hợp lệ
   */
  @GetMapping(ENDPOINT_CHECK_TOKEN)
  ApiResponse<AuthResponse> checkToken(
      @RequestHeader(value = "Authorization", required = false) String bearer) {
    AuthResponse result = authService.checkToken(bearer);
    return ApiResponse.<AuthResponse>builder()
        .message(MSG_CHECK_TOKEN)
        .result(result)
        .build();
  }
}
