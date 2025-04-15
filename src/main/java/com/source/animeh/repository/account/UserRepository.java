package com.source.animeh.repository.account;

import com.source.animeh.entity.account.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link User} cho chức năng quản lý người dùng.
 */
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Tìm kiếm {@link User} dựa trên username.
   *
   * @param username tên đăng nhập của {@link User} cần tìm kiếm.
   * @return {@code Optional<User>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có kết quả.
   */
  Optional<User> findByUsername(String username);

  /**
   * Tìm kiếm {@link User} dựa trên email.
   *
   * @param email email của {@link User} cần tìm kiếm.
   * @return {@code Optional<User>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có kết quả.
   */
  Optional<User> findByEmail(String email);
}
