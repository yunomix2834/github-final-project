package com.source.animeh.repository.account;

import com.source.animeh.entity.account.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link Role} dùng cho chức năng phân quyền.
 */
public interface RoleRepository extends JpaRepository<Role, String> {

  /**
   * Tìm kiếm {@link Role} dựa trên tên.
   *
   * @param name tên của {@link Role} cần tìm kiếm.
   * @return {@code Optional<Role>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có kết quả.
   */
  Optional<Role> findByName(String name);
}
