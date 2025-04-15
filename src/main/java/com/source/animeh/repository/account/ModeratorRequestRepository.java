package com.source.animeh.repository.account;

import com.source.animeh.entity.account.ModeratorRequest;
import com.source.animeh.entity.account.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link ModeratorRequest} dùng cho các yêu cầu moderator.
 */
public interface ModeratorRequestRepository extends JpaRepository<ModeratorRequest, String> {

  // Hằng số cho JPQL query tìm ModeratorRequest theo userId
  String QUERY_FIND_BY_USER_ID =
      "SELECT mr FROM ModeratorRequest mr WHERE mr.user.id = :userId ORDER BY mr.createdAt DESC";
  // Hằng số cho tên parameter của userId
  String PARAM_USER_ID = "userId";

  /**
   * Tìm danh sách {@link ModeratorRequest} theo trạng thái và sắp xếp theo thời gian tạo giảm dần,
   * có hỗ trợ phân trang.
   *
   * @param status   trạng thái của ModeratorRequest cần tìm
   * @param pageable thông tin phân trang
   * @return danh sách {@link ModeratorRequest} thoả mãn điều kiện
   */
  List<ModeratorRequest> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

  /**
   * Tìm {@link ModeratorRequest} đầu tiên theo {@link User} và trạng thái, sắp xếp theo thời gian
   * tạo giảm dần.
   *
   * @param user   đối tượng {@link User} submit request
   * @param status trạng thái của ModeratorRequest cần tìm
   * @return {@link ModeratorRequest} đầu tiên thoả mãn điều kiện hoặc null nếu không tìm thấy
   */
  ModeratorRequest findFirstByUserAndStatusOrderByCreatedAtDesc(User user, String status);

  /**
   * Lấy về tất cả các ModeratorRequest theo userId, có hỗ trợ phân trang.
   *
   * @param userId   mã định danh của {@link User}
   * @param pageable thông tin phân trang
   * @return trang kết quả chứa {@link ModeratorRequest} được sắp xếp theo thời gian tạo giảm dần
   */
  @Query(QUERY_FIND_BY_USER_ID)
  Page<ModeratorRequest> findByUserId(@Param(PARAM_USER_ID) String userId, Pageable pageable);
}
