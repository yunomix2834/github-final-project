package com.source.animeh.data;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lớp {@code UserSettingId} đại diện cho định danh của cài đặt người dùng liên quan đến anime.
 * <p>
 * Lớp này được sử dụng để kết hợp định danh của người dùng và anime, thường dùng trong các bảng
 * liên kết.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSettingId implements Serializable {

  /**
   * Định danh của người dùng.
   */
  String user;

  /**
   * Định danh của anime.
   */
  String anime;
}
