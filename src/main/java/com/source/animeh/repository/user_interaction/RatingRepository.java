package com.source.animeh.repository.user_interaction;

import com.source.animeh.entity.user_interaction.Rating;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link Rating} dùng cho chức năng đánh giá anime.
 */
public interface RatingRepository extends JpaRepository<Rating, String> {

  // Hằng số cho tên parameter
  String PARAM_ANIME_ID = "animeId";

  // Hằng số truy vấn JPQL tính trung bình giá trị đánh giá theo animeId
  String QUERY_FIND_AVERAGE_BY_ANIME_ID =
      "SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.anime.id = :" + PARAM_ANIME_ID;

  /**
   * Tìm kiếm đối tượng {@link Rating} dựa trên mã định danh của anime và người dùng.
   *
   * @param animeId mã định danh của anime
   * @param userId  mã định danh của người dùng
   * @return {@code Optional<Rating>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không tồn tại
   */
  Optional<Rating> findByAnimeIdAndUserId(String animeId, String userId);

  /**
   * Tính trung bình giá trị {@code value} của các {@link Rating} cho một anime cụ thể.
   *
   * @param animeId mã định danh của anime
   * @return giá trị trung bình của {@link Rating} dưới dạng {@link BigDecimal}; nếu không có đánh
   * giá nào thì trả về 0.
   */
  @Query(QUERY_FIND_AVERAGE_BY_ANIME_ID)
  BigDecimal findAverageByAnimeId(@Param(PARAM_ANIME_ID) String animeId);
}
