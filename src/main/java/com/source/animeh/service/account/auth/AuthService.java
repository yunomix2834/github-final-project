package com.source.animeh.service.account.auth;

import static com.source.animeh.constant.PredefinedTime.JWT_EXPIRY;
import static com.source.animeh.constant.PredefinedTime.JWT_REFRESH_EXPIRY;
import static com.source.animeh.constant.PredefinedTime.WEEK_MILLISECONDS;

import com.source.animeh.constant.account.PredefinedRole;
import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ForgotPasswordRequest;
import com.source.animeh.dto.request.account.auth.forgot_resetpassword.ResetPasswordRequest;
import com.source.animeh.dto.request.account.auth.login.AuthRequestEmailLogin;
import com.source.animeh.dto.request.account.auth.login.AuthRequestUsernameLogin;
import com.source.animeh.dto.request.account.auth.register.AuthRequestRegister;
import com.source.animeh.dto.response.account.auth.AuthResponse;
import com.source.animeh.entity.account.Role;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.account.auth.PasswordResetToken;
import com.source.animeh.entity.account.auth.RefreshToken;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.account.auth.AuthServiceInterface;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.account.auth.PasswordResetTokenRepository;
import com.source.animeh.repository.account.auth.RefreshTokenRepository;
import com.source.animeh.service.account.RoleService;
import com.source.animeh.service.account.UserService;
import com.source.animeh.service.mail.MailResetEmailSenderService;
import com.source.animeh.utils.JwtUtils;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements AuthServiceInterface {

  UserRepository userRepository;
  AuthenticationManager authenticationManager;
  PasswordEncoder passwordEncoder;
  JwtUtils jwtUtils;
  RoleService roleService;
  UserService userService;
  PasswordResetTokenRepository pwResetTokenRepository;
  MailResetEmailSenderService mailResetEmailSenderService;
  RefreshTokenRepository refreshTokenRepository;

  // Đăng ký
  @Override
  public AuthResponse register(
      AuthRequestRegister request) {

    // Kiểm tra userName/email đã tồn tại hay chưa
    if (userRepository.findByUsername(
        request.getUsername()).isPresent()) {
      throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
    }
    if (userRepository.findByEmail(
        request.getEmail()).isPresent()) {
      throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    Role userRole = roleService.findRoleByName(PredefinedRole.USER);

    // Tạo user
    User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(
        request.getPassword()));
    user.setRole(userRole); // Mặc định assign
    user.setCreatedAt(LocalDateTime.now());
    user.setIsActive(true);

    userRepository.save(user);

    // Tạo JWT
    String accessToken = jwtUtils.generateToken(
        user.getUsername(),
        user.getRole().getName());

    // Tạo Refresh Token => lưu DB
    String refreshValue = UUID.randomUUID().toString();
    createRefreshToken(refreshValue, user, accessToken);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .tokenAccessType("Bearer")
        .tokenId(refreshValue)
        .refreshValue(refreshValue)
        .expiryTime(JWT_EXPIRY)
        .isAuthenticated(true)
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole().getName())
        .build();
  }

  /**
   * Xử lý login, set cookie HttpOnly: access_token & refresh_token
   */
  @Override
  public AuthResponse loginByUsername(
      AuthRequestUsernameLogin authRequestUsernameLogin) {

    // Check Username password
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequestUsernameLogin.getUsername(),
              authRequestUsernameLogin.getPassword())
      );
    } catch (BadCredentialsException ex) {
      throw new AppException(ErrorCode.INVALID_USERNAME_PASSWORD);
    }

    // Lấy user
    User user = userService.findByUsername(
        authRequestUsernameLogin.getUsername());

    // Tạo access token
    String accessToken = jwtUtils.generateToken(
        user.getUsername(),
        user.getRole().getName());

    // Tạo Refresh Token lưu vào db
    String refreshValue = UUID.randomUUID().toString();
    createRefreshToken(refreshValue, user, accessToken);

    return AuthResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole().getName())
        .avatarUrl(user.getAvatarUrl())
        .accessToken(accessToken)
        .tokenAccessType("Bearer")
        .tokenId(refreshValue)
        .refreshValue(refreshValue)
        .expiryTime(JWT_EXPIRY)
        .isAuthenticated(true)
        .build();
  }

  @Override
  public AuthResponse loginByEmail(
      AuthRequestEmailLogin authRequestEmailLogin) {

    // Check Username password
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequestEmailLogin.getEmail(),
              authRequestEmailLogin.getPassword())
      );
    } catch (BadCredentialsException ex) {
      throw new AppException(ErrorCode.INVALID_USERNAME_PASSWORD);
    }

    // Lấy user
    User user = userService.findByEmail(
        authRequestEmailLogin.getEmail());

    // Tạo access token
    String accessToken = jwtUtils.generateToken(
        user.getUsername(),
        user.getRole().getName());

    // Tạo Refresh Token lưu vào db
    String refreshValue = UUID.randomUUID().toString();
    createRefreshToken(refreshValue, user, accessToken);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .tokenAccessType("Bearer")
        .tokenId(refreshValue)
        .refreshValue(refreshValue)
        .expiryTime(JWT_EXPIRY)
        .isAuthenticated(true)
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole().getName())
        .build();
  }

  /**
   * Refresh token
   */
  @Override
  public AuthResponse refreshToken(String refreshValue) {

    if (refreshValue == null) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    RefreshToken rt = refreshTokenRepository
        .findById(refreshValue)
        .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

    if (Boolean.TRUE.equals(rt.getIsRevoked())) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    if (rt.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new AppException(ErrorCode.TOKEN_EXPIRED);
    }

    User user = rt.getUser();
    String newAccessToken = jwtUtils.generateToken(
        user.getUsername(),
        user.getRole().getName(),
        WEEK_MILLISECONDS
    );

    rt.setToken(newAccessToken);
    refreshTokenRepository.save(rt);

    return AuthResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getRole().getName())
        .accessToken(newAccessToken)
        .tokenAccessType("Bearer")
        .tokenId(refreshValue)
        .refreshValue(refreshValue)
        .expiryTime(JWT_REFRESH_EXPIRY)
        .isAuthenticated(true)
        .build();
  }

  /**
   * Logout
   */
  @Override
  public AuthResponse logout(String refreshValue) {

    if (refreshValue != null) {
      refreshTokenRepository.findById(refreshValue)
          .ifPresent(refreshToken -> {
            refreshToken.setIsRevoked(true);
            refreshToken.setExpiredAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
          });
    }

    return AuthResponse.builder()
        .isAuthenticated(false)
        .build();
  }

  // Forgot Password
  @Override
  public void forgotPassword(
      ForgotPasswordRequest forgotPasswordRequest) {

    User user = userService.findByEmail(
        forgotPasswordRequest.getEmail());

    // Tạo token
    String tokenValue = UUID.randomUUID().toString();
    LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

    // Lưu vào bảng PASSWORD_RESET_TOKEN
    PasswordResetToken prt = new PasswordResetToken();
    prt.setId(UUID.randomUUID().toString());
    prt.setToken(tokenValue);
    prt.setExpiredAt(expiredAt);
    prt.setUser(user);
    prt.setIsUsed(false);

    pwResetTokenRepository.save(prt);

    // Send Reset Email
    mailResetEmailSenderService.sendResetEmail(user, tokenValue);
  }

  // Reset Password
  @Override
  public void resetPassword(
      ResetPasswordRequest resetPasswordRequest) {

    // Tìm passwordResetToken
    PasswordResetToken prt = pwResetTokenRepository
        .findByToken(resetPasswordRequest.getToken())
        .orElseThrow(
            () -> new AppException(ErrorCode.INVALID_TOKEN));

    // Check Expiry
    if (prt.getExpiredAt().isBefore(LocalDateTime.now())) {
      pwResetTokenRepository.delete(prt);
      throw new AppException(ErrorCode.TOKEN_EXPIRED);
    }

    // Check isUsed
    if (Boolean.TRUE.equals(prt.getIsUsed())) {
      throw new AppException(ErrorCode.TOKEN_ALREADY_USED);
    }

    User user = prt.getUser();
    user.setPassword(passwordEncoder.encode(
        resetPasswordRequest.getNewPassword()));
    userRepository.save(user);

    // Xoá hoặc đánh dấu token
    pwResetTokenRepository.delete(prt);
    // passwordResetToken.setIsUsed(true);
    // pwResetTokenRepository.save(passwordResetToken);
  }

  /**
   * Check Access Token còn hợp lệ hay không
   */
  @Override
  public AuthResponse checkToken(String bearer) {

    if (bearer == null || !bearer.startsWith("Bearer ")) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    String token = bearer.substring(7);

    // Validate token
    // TODO Validate không thành công - trả ra mã lỗi (đã trả ra nhưng lỗi)
    var claims = jwtUtils.validateTokenAndGetClaims(token);
    if (claims == null) {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    // Lấy username từ subject của token
    String username = claims.getSubject();

    // Tìm user trên DB
    User user = userService.findByUsername(username);

    return AuthResponse.builder()
        .isAuthenticated(true)
        .role(user.getRole().getName())
        .build();
  }

  void createRefreshToken(
      String refreshValue,
      User user,
      String accessToken) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setId(refreshValue);
    refreshToken.setUser(user);
    refreshToken.setToken(accessToken);
    refreshToken.setExpiredAt(JWT_EXPIRY);
    refreshToken.setIsRevoked(false);
    refreshToken.setCreatedAt(LocalDateTime.now());
    refreshTokenRepository.save(refreshToken);
  }
}
