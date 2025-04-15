package com.source.animeh.configuration.config;

import com.source.animeh.constant.account.PredefinedRole;
import com.source.animeh.entity.account.Role;
import com.source.animeh.entity.account.User;
import com.source.animeh.repository.account.RoleRepository;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.service.account.RoleService;
import com.source.animeh.service.account.UserService;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình ứng dụng khởi tạo các Role và tài khoản Admin mặc định.
 * <p>
 * Lớp này thực hiện việc khởi tạo các Role (ADMIN, MOD, USER) nếu chưa tồn tại và tạo tài khoản
 * Admin mặc định nếu chưa có, dựa trên các cấu hình đã định nghĩa.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationConfig {

  // Các hằng số mặc định cho tài khoản Admin
  private static final String DEFAULT_ADMIN_USERNAME = "yunom280304";
  private static final String DEFAULT_ADMIN_PASSWORD = "Dinhanst2832004%";
  private static final String DEFAULT_ADMIN_EMAIL = "yunom280304@gmail.com";

  // Các hằng số cho thông điệp log
  private static final String INITIALIZING_MESSAGE = "Initializing application.....";
  private static final String INIT_COMPLETED_MESSAGE = "Application initialization completed .....";
  private static final String ADMIN_CREATED_MESSAGE = "Admin user '{}' has been created with default password='{}', email='{}'. Please change it!";

  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  RoleRepository roleRepository;
  UserService userService;
  RoleService roleService;

  /**
   * Bean ApplicationRunner được chạy khi ứng dụng khởi động.
   * <p>
   * Bean này chỉ được khởi tạo nếu thuộc tính <code>spring.datasource.driver-class-name</code> có
   * giá trị là <code>com.microsoft.sqlserver.jdbc.SQLServerDriver</code>.
   * </p>
   *
   * @return ApplicationRunner thực hiện khởi tạo các Role và Admin User mặc định.
   */
  @Bean
  @ConditionalOnProperty(
      prefix = "spring.datasource",
      value = "driver-class-name",
      havingValue = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  )
  ApplicationRunner applicationRunner() {
    log.info(INITIALIZING_MESSAGE);
    return this::run;
  }

  /**
   * Thực hiện khởi tạo các Role và tạo tài khoản Admin mặc định nếu chưa tồn tại.
   *
   * @param args tham số của ứng dụng (không sử dụng)
   */
  private void run(ApplicationArguments args) {
    Role adminRole = null;
    if (roleService.findRoleByNameThrowNull(PredefinedRole.ADMIN) == null) {
      adminRole = initialRole(PredefinedRole.ADMIN, PredefinedRole.ADMIN_DESCRIPTION);
    }
    if (roleService.findRoleByNameThrowNull(PredefinedRole.MOD) == null) {
      initialRole(PredefinedRole.MOD, PredefinedRole.MODERATOR_DESCRIPTION);
    }
    if (roleService.findRoleByNameThrowNull(PredefinedRole.USER) == null) {
      initialRole(PredefinedRole.USER, PredefinedRole.USER_DESCRIPTION);
    }

    // Nếu chưa có tài khoản Admin => tạo tài khoản Admin mặc định
    if (userService.findByUsernameNotThrowError(DEFAULT_ADMIN_USERNAME) == null) {
      User admin = new User();
      admin.setId(UUID.randomUUID().toString());
      admin.setUsername(DEFAULT_ADMIN_USERNAME);
      admin.setEmail(DEFAULT_ADMIN_EMAIL);
      admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
      admin.setRole(adminRole);
      admin.setIsActive(true);
      userRepository.save(admin);
      log.warn(ADMIN_CREATED_MESSAGE, DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD,
          DEFAULT_ADMIN_EMAIL);
    }
    log.info(INIT_COMPLETED_MESSAGE);
  }

  /**
   * Khởi tạo và lưu một Role mới dựa trên tên và mô tả.
   *
   * @param roleName        tên của Role cần khởi tạo
   * @param roleDescription mô tả của Role
   * @return đối tượng {@link Role} sau khi được lưu vào cơ sở dữ liệu
   */
  private Role initialRole(String roleName, String roleDescription) {
    Role role = new Role();
    role.setId(UUID.randomUUID().toString());
    role.setName(roleName);
    role.setDescription(roleDescription);
    return roleRepository.save(role);
  }
}
