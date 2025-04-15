package com.source.animeh.repository.user_interaction;

import com.source.animeh.entity.user_interaction.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link Comment} dùng cho chức năng tương tác của người dùng.
 */
public interface CommentRepository extends JpaRepository<Comment, String> {

  // Hằng số cho tên các parameter
  String PARAM_ANIME_ID = "animeId";
  String PARAM_PARENT_ID = "parentId";

  // Hằng số query JPQL
  String QUERY_FIND_ALL_COMMENTS_BY_ANIME =
      "SELECT c FROM Comment c WHERE c.anime.id = :" + PARAM_ANIME_ID
          + " ORDER BY c.createdAt DESC";
  String QUERY_FIND_ALL_ACTIVE_COMMENTS_BY_ANIME =
      "SELECT c FROM Comment c WHERE c.anime.id = :" + PARAM_ANIME_ID
          + " AND c.isDeactivate = false ORDER BY c.createdAt DESC";
  String QUERY_FIND_ALL_ROOT_COMMENTS_BY_ANIME =
      "SELECT c FROM Comment c WHERE c.anime.id = :" + PARAM_ANIME_ID
          + " AND c.parent IS NULL ORDER BY c.createdAt DESC";
  String QUERY_FIND_ALL_ACTIVE_ROOT_COMMENTS_BY_ANIME =
      "SELECT c FROM Comment c WHERE c.anime.id = :" + PARAM_ANIME_ID
          + " AND c.isDeactivate = false AND c.parent IS NULL ORDER BY c.createdAt DESC";
  String QUERY_FIND_REPLIES =
      "SELECT c FROM Comment c WHERE c.parent = :" + PARAM_PARENT_ID + " ORDER BY c.createdAt ASC";

  /**
   * Tìm tất cả các comment chưa bị ẩn của một anime, sắp xếp theo thời gian tạo tăng dần.
   *
   * @param animeId mã định danh của anime
   * @return danh sách {@link Comment} chưa bị ẩn, sắp xếp theo thời gian tạo tăng dần
   */
  List<Comment> findByAnimeIdAndIsDeactivateFalseOrderByCreatedAtAsc(String animeId);

  /**
   * Tìm tất cả các comment của một anime (bao gồm cả comment đã bị ẩn), sắp xếp theo thời gian tạo
   * tăng dần.
   *
   * @param animeId mã định danh của anime
   * @return danh sách {@link Comment} của anime, sắp xếp theo thời gian tạo tăng dần
   */
  List<Comment> findByAnimeIdOrderByCreatedAtAsc(String animeId);

  /**
   * Lấy tất cả comment của một anime với phân trang, sắp xếp theo thời gian tạo giảm dần.
   *
   * @param animeId  mã định danh của anime
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách {@link Comment} được sắp xếp theo thời gian tạo giảm dần
   */
  @Query(QUERY_FIND_ALL_COMMENTS_BY_ANIME)
  Page<Comment> findAllCommentsByAnime(@Param(PARAM_ANIME_ID) String animeId, Pageable pageable);

  /**
   * Lấy tất cả comment chưa bị ẩn của một anime với phân trang, sắp xếp theo thời gian tạo giảm
   * dần.
   *
   * @param animeId  mã định danh của anime
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách {@link Comment} chưa bị ẩn, sắp xếp theo thời gian tạo giảm dần
   */
  @Query(QUERY_FIND_ALL_ACTIVE_COMMENTS_BY_ANIME)
  Page<Comment> findAllActiveCommentsByAnime(@Param(PARAM_ANIME_ID) String animeId,
      Pageable pageable);

  /**
   * Lấy tất cả comment gốc (không có phản hồi) của một anime với phân trang, sắp xếp theo thời gian
   * tạo giảm dần.
   *
   * @param animeId  mã định danh của anime
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách các comment gốc, sắp xếp theo thời gian tạo giảm dần
   */
  @Query(QUERY_FIND_ALL_ROOT_COMMENTS_BY_ANIME)
  Page<Comment> findAllRootCommentsByAnime(@Param(PARAM_ANIME_ID) String animeId,
      Pageable pageable);

  /**
   * Lấy tất cả comment gốc chưa bị ẩn của một anime với phân trang, sắp xếp theo thời gian tạo giảm
   * dần.
   *
   * @param animeId  mã định danh của anime
   * @param pageable thông tin phân trang
   * @return trang chứa danh sách các comment gốc chưa bị ẩn, sắp xếp theo thời gian tạo giảm dần
   */
  @Query(QUERY_FIND_ALL_ACTIVE_ROOT_COMMENTS_BY_ANIME)
  Page<Comment> findAllActiveRootCommentsByAnime(@Param(PARAM_ANIME_ID) String animeId,
      Pageable pageable);

  /**
   * Tìm các phản hồi (reply) của một comment dựa trên parentId.
   *
   * @param parentId mã định danh của comment cha
   * @return danh sách {@link Comment} là phản hồi, sắp xếp theo thời gian tạo tăng dần
   */
  @Query(QUERY_FIND_REPLIES)
  List<Comment> findReplies(@Param(PARAM_PARENT_ID) String parentId);
}
