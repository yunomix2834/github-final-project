package com.source.animeh.configuration.filter;

import com.source.animeh.entity.account.User;
import com.source.animeh.repository.account.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service tùy chỉnh thực hiện việc tải thông tin người dùng cho Spring Security.
 * <p>
 * Dựa trên username hoặc email, service này sẽ tìm kiếm đối tượng {@link User} thông qua
 * {@link UserRepository} và chuyển đổi sang {@link UserDetails} để sử dụng cho quá trình xác thực.
 * </p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomUserDetailsService implements UserDetailsService {

  // Hằng số dùng để xác định email
  private static final String EMAIL_IDENTIFIER = "@";
  // Hằng số thông báo lỗi khi không tìm thấy người dùng
  private static final String USER_NOT_FOUND_MESSAGE = "User not found with: ";
  UserRepository userRepository;

  /**
   * Tải thông tin người dùng dựa trên username hoặc email.
   *
   * @param userNameOrEmail tên đăng nhập hoặc email của người dùng
   * @return {@link UserDetails} chứa thông tin người dùng
   * @throws UsernameNotFoundException nếu không tìm thấy người dùng với thông tin đã cung cấp
   */
  @Override
  public UserDetails loadUserByUsername(String userNameOrEmail) throws UsernameNotFoundException {
    Optional<User> userOpt;
    if (userNameOrEmail.contains(EMAIL_IDENTIFIER)) {
      // Nếu chuỗi chứa dấu '@' thì xem như là email
      userOpt = userRepository.findByEmail(userNameOrEmail);
    } else {
      // Ngược lại, xem như là username
      userOpt = userRepository.findByUsername(userNameOrEmail);
    }

    User user = userOpt.orElseThrow(() ->
        new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE + userNameOrEmail)
    );

    // Chuyển đổi đối tượng User sang UserDetails
    return buildUserDetails(user);
  }

  /**
   * Xây dựng đối tượng {@link UserDetails} từ đối tượng {@link User}.
   *
   * @param user đối tượng {@code User} cần chuyển đổi
   * @return {@link UserDetails} chứa thông tin cần thiết cho quá trình xác thực
   */
  private UserDetails buildUserDetails(User user) {
    List<GrantedAuthority> authorities = Collections.emptyList();
    if (user.getRole() != null) {
      authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),     // tên đăng nhập
        user.getPassword(),     // mật khẩu đã mã hoá
        user.getIsActive(),     // trạng thái kích hoạt (enabled)
        true,                   // accountNonExpired
        true,                   // credentialsNonExpired
        true,                   // accountNonLocked
        authorities
    );
  }
}
