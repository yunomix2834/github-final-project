package com.source.animeh.utils;

import static com.source.animeh.constant.account.PredefinedRole.ROLE_HIERARCHY_RULES;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình các bean cần thiết cho hệ thống bảo mật.
 * <p>
 * Bao gồm: {@link AuthenticationManager}, {@link PasswordEncoder} và {@link RoleHierarchy}.
 * </p>
 */
@Configuration
public class BeanConfig {

  /**
   * Tạo bean {@link AuthenticationManager} để quản lý quá trình xác thực.
   *
   * @param config cấu hình xác thực
   * @return {@link AuthenticationManager} được cấu hình từ {@code config}
   * @throws Exception nếu có lỗi xảy ra khi lấy {@code AuthenticationManager}
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Tạo bean {@link PasswordEncoder} để mã hóa mật khẩu sử dụng thuật toán BCrypt.
   *
   * @return {@link PasswordEncoder} sử dụng BCrypt
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Cấu hình bean {@link RoleHierarchy} để xác định phân cấp của các vai trò.
   * <p>
   * Phân cấp vai trò: ROLE_ADMIN > ROLE_MODERATOR > ROLE_USER.
   * </p>
   *
   * @return {@link RoleHierarchy} được cấu hình theo {@code ROLE_HIERARCHY_RULES}
   */
  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(ROLE_HIERARCHY_RULES);
  }
}
