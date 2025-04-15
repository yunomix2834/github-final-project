package com.source.animeh.repository.account.auth;

import com.source.animeh.entity.account.auth.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link RefreshToken} dùng cho chức năng xác thực và làm mới token.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

  /**
   * Tìm kiếm {@link RefreshToken} dựa trên token.
   *
   * @param token token của {@link RefreshToken} cần tìm kiếm.
   * @return {@code Optional<RefreshToken>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có kết
   * quả.
   */
  Optional<RefreshToken> findByToken(String token);
}
