package com.source.animeh.utils;

import com.source.animeh.entity.account.User;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.repository.account.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Lớp tiện ích cho các thao tác bảo mật, bao gồm phương thức để truy cập thông tin người dùng và
 * vai trò, cũng như tạo cookie bảo mật HTTP-only.
 */
public class SecurityUtils {

  /**
   * Trả về đối tượng người dùng hiện đang được xác thực từ cơ sở dữ liệu hoặc null nếu không tìm
   * thấy hoặc chưa đăng nhập.
   *
   * @param userRepository kho lưu trữ dùng để truy vấn dữ liệu người dùng
   * @return đối tượng User hoặc null nếu không xác thực hoặc không tìm thấy
   */
  public static User getCurrentUser(UserRepository userRepository) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails) {
      String username = ((UserDetails) principal).getUsername();
      return userRepository.findByUsername(username)
          .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    if (principal instanceof User) {
      return (User) principal;
    }

    return null;
  }

  /**
   * Lấy vai trò của người dùng hiện tại như "ROLE_ADMIN", "ROLE_MODERATOR",... hoặc trả về null nếu
   * người dùng chưa đăng nhập.
   *
   * @param userRepository kho lưu trữ dùng để truy vấn dữ liệu người dùng
   * @return tên vai trò của người dùng hiện tại hoặc null nếu chưa đăng nhập
   */
  public static String getCurrentUserRole(UserRepository userRepository) {
    User currentUser = getCurrentUser(userRepository);
    if (currentUser != null && currentUser.getRole() != null) {
      return currentUser.getRole().getName();
    }
    return null;
  }

  /**
   * Tạo cookie HTTP-only với tên và giá trị đã cho và thời gian sống tối đa.
   *
   * @param name          tên của cookie
   * @param value         giá trị của cookie
   * @param maxAgeSeconds thời gian sống tối đa của cookie tính bằng giây
   * @return đối tượng Cookie đã được tạo
   */
  public static Cookie createHttpOnlyCookie(String name, String value, int maxAgeSeconds) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAgeSeconds);
    // Bỏ comment dòng dưới đây nếu sử dụng HTTPS
    // cookie.setSecure(true);
    return cookie;
  }
}
