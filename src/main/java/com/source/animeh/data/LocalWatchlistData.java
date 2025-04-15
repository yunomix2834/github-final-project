package com.source.animeh.data;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lớp {@code LocalWatchlistData} đại diện cho thông tin lịch sử thêm anime vào watchlist.
 * <p>
 * Lớp này chứa các thông tin sau:
 * <ul>
 *   <li><b>animeId:</b> Mã định danh của anime.</li>
 *   <li><b>dateAdded:</b> Thời điểm anime được thêm vào watchlist.</li>
 * </ul>
 * </p>
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalWatchlistData {

  /**
   * Mã định danh của anime.
   */
  String animeId;

  /**
   * Thời điểm anime được thêm vào watchlist.
   */
  LocalDateTime dateAdded;
}
