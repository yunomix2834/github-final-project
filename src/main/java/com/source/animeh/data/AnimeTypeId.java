package com.source.animeh.data;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lớp {@code AnimeTypeId} đại diện cho định danh của một anime và loại của nó.
 * <p>
 * Lớp này được sử dụng khi cần xác định mối quan hệ giữa một anime và một type, ví dụ trong các
 * bảng liên kết hoặc để truyền tải thông tin định danh.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimeTypeId implements Serializable {

  /**
   * Định danh của anime.
   */
  String anime;

  /**
   * Định danh của type.
   */
  String type;
}
