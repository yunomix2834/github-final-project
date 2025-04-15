package com.source.animeh.repository.account.auth;

import com.source.animeh.entity.account.auth.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link PasswordResetToken} dùng cho chức năng đặt lại mật khẩu.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

  /**
   * Tìm kiếm {@link PasswordResetToken} dựa trên token.
   *
   * @param token token của {@link PasswordResetToken} cần tìm.
   * @return {@code Optional<PasswordResetToken>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không
   * tìm thấy.
   */
  Optional<PasswordResetToken> findByToken(String token);
}
