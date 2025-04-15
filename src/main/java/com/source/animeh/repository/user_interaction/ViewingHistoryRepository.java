package com.source.animeh.repository.user_interaction;

import com.source.animeh.entity.user_interaction.ViewingHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link ViewingHistory} dùng cho chức năng theo dõi lịch sử xem.
 */
public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, String> {

  // Hằng số cho tên các parameter
  String PARAM_USER_ID = "userId";
  String PARAM_EPISODE_ID = "episodeId";

  // Hằng số cho trạng thái của tập phim đã phê duyệt
  String STATUS_APPROVED = "approved";

  // Hằng số truy vấn JPQL lấy các lịch sử xem đã được phê duyệt của người dùng
  String QUERY_FIND_APPROVED_BY_USER = "SELECT vh FROM ViewingHistory vh "
      + "WHERE vh.user.id = :" + PARAM_USER_ID + " "
      + "AND vh.isDeleted = false "
      + "AND vh.episode.status = '" + STATUS_APPROVED + "' "
      + "ORDER BY vh.watchedDate DESC";

  /**
   * Tìm tất cả lịch sử xem của người dùng mà chưa bị xóa, sắp xếp theo thời gian xem giảm dần.
   *
   * @param userId mã định danh của người dùng
   * @return danh sách {@link ViewingHistory} của người dùng
   */
  List<ViewingHistory> findByUserIdAndIsDeletedFalseOrderByWatchedDateDesc(String userId);

  /**
   * Tìm một lịch sử xem theo mã người dùng và mã tập phim.
   *
   * @param userId    mã định danh của người dùng
   * @param episodeId mã định danh của tập phim
   * @return {@code Optional<ViewingHistory>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có
   */
  Optional<ViewingHistory> findByUserIdAndEpisodeId(String userId, String episodeId);

  /**
   * Tìm một lịch sử xem chưa bị xóa theo mã người dùng và mã tập phim.
   *
   * @param userId    mã định danh của người dùng
   * @param episodeId mã định danh của tập phim
   * @return {@code Optional<ViewingHistory>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không có
   */
  Optional<ViewingHistory> findByUserIdAndEpisodeIdAndIsDeletedFalse(String userId,
      String episodeId);

  /**
   * Tìm tất cả lịch sử xem theo mã người dùng và mã tập phim.
   *
   * @param userId    mã định danh của người dùng
   * @param episodeId mã định danh của tập phim
   * @return danh sách {@link ViewingHistory} tương ứng
   */
  List<ViewingHistory> findAllByUserIdAndEpisodeId(String userId, String episodeId);

  /**
   * Lấy các lịch sử xem đã được phê duyệt của người dùng với phân trang.
   * <p>
   * Chỉ trả về các record mà không bị xóa, và tập phim có trạng thái {@code approved}.
   * </p>
   *
   * @param userId   mã định danh của người dùng
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách {@link ViewingHistory} đã được phê duyệt, sắp xếp theo thời gian
   * xem giảm dần
   */
  @Query(QUERY_FIND_APPROVED_BY_USER)
  Page<ViewingHistory> findApprovedByUserId(@Param(PARAM_USER_ID) String userId, Pageable pageable);
}
