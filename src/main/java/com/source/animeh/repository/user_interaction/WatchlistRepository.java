package com.source.animeh.repository.user_interaction;

import com.source.animeh.entity.user_interaction.Watchlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link Watchlist} dùng cho chức năng danh sách theo dõi (watchlist)
 * của người dùng.
 */
public interface WatchlistRepository extends JpaRepository<Watchlist, String> {

  // Hằng số cho tên các parameter
  String PARAM_USER_ID = "userId";
  String PARAM_ANIME_ID = "animeId";

  // Hằng số cho trạng thái của anime đã được phê duyệt
  String STATUS_APPROVED = "approved";

  // Hằng số truy vấn JPQL lấy danh sách watchlist của người dùng với anime đã được phê duyệt
  String QUERY_FIND_APPROVED_WATCHLIST_BY_USER_ID = "SELECT w FROM Watchlist w "
      + "WHERE w.user.id = :" + PARAM_USER_ID + " "
      + "  AND w.anime.statusSubmitted = '" + STATUS_APPROVED + "' "
      + "ORDER BY w.createdAt DESC";

  /**
   * Tìm tất cả các {@link Watchlist} của một người dùng.
   *
   * @param userId mã định danh của người dùng
   * @return danh sách {@link Watchlist} của người dùng
   */
  List<Watchlist> findByUserId(String userId);

  /**
   * Kiểm tra xem một anime đã có trong danh sách theo dõi của người dùng hay chưa.
   *
   * @param userId  mã định danh của người dùng
   * @param animeId mã định danh của anime
   * @return {@code Optional<Watchlist>} chứa đối tượng nếu tồn tại, hoặc rỗng nếu không tồn tại
   */
  Optional<Watchlist> findByUserIdAndAnimeId(String userId, String animeId);

  /**
   * Lấy danh sách watchlist của người dùng với các anime đã được phê duyệt, sắp xếp theo thứ tự
   * giảm dần theo thời gian tạo.
   *
   * @param userId   mã định danh của người dùng
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách {@link Watchlist} với các anime đã được phê duyệt
   */
  @Query(QUERY_FIND_APPROVED_WATCHLIST_BY_USER_ID)
  Page<Watchlist> findApprovedWatchlistByUserId(@Param(PARAM_USER_ID) String userId,
      Pageable pageable);

  @Query("SELECT COUNT(w) FROM Watchlist w WHERE w.anime.id = :animeId")
  long countByAnimeId(@Param("animeId") String animeId);
}
