package com.source.animeh.repository.film_series;

import com.source.animeh.entity.film_series.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link Type} dùng cho chức năng phân loại trong hệ thống phim.
 */
public interface TypeRepository extends JpaRepository<Type, String> {

  /**
   * Tìm kiếm {@link Type} theo tên và loại.
   *
   * @param name tên của type
   * @param type loại của type
   * @return {@code Optional<Type>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không tồn tại
   */
  Optional<Type> findByNameAndType(String name, String type);

  /**
   * Kiểm tra sự tồn tại của {@link Type} theo tên và loại.
   *
   * @param name tên của type
   * @param type loại của type
   * @return {@code true} nếu tồn tại, {@code false} nếu không tồn tại
   */
  boolean existsByNameAndType(String name, String type);

  /**
   * Tìm kiếm {@link Type} theo tên.
   *
   * @param name tên của type
   * @return {@code Optional<Type>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có kết quả
   */
  Optional<Type> findByName(String name);

  /**
   * Tìm kiếm danh sách {@link Type} theo loại.
   *
   * @param type loại của type
   * @return danh sách các {@link Type} có loại tương ứng
   */
  List<Type> findByType(String type);
}
