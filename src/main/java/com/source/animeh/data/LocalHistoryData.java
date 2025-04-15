package com.source.animeh.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lớp {@code LocalHistoryData} đại diện cho thông tin lịch sử xem tập phim.
 * <p>
 * Lớp này chứa các thông tin sau:
 * <ul>
 *   <li>Mã định danh của tập phim (episodeId)</li>
 *   <li>Thời điểm xem tập phim (watchedDate)</li>
 *   <li>Thời lượng xem tập phim (watchedDuration)</li>
 * </ul>
 * </p>
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalHistoryData {

  /**
   * Mã định danh của tập phim.
   */
  String episodeId;

  /**
   * Thời điểm xem tập phim.
   */
  LocalDateTime watchedDate;

  /**
   * Thời lượng xem tập phim.
   */
  BigDecimal watchedDuration;
}
