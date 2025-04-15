package com.source.animeh.repository.film_series;

import com.source.animeh.entity.film_series.Anime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository quản lý thực thể {@link Anime} cho chức năng xử lý và truy vấn thông tin phim.
 */
public interface AnimeRepository extends JpaRepository<Anime, String>,
    JpaSpecificationExecutor<Anime> {

  // Hằng số trạng thái được duyệt
  String APPROVED_STATUS = "approved";

  // Hằng số giới hạn số lượng phim khi truy vấn top phim
  int TOP_LIMIT = 4;

  // Hằng số query native để lấy top 4 phim có lượt view cao nhất
  String QUERY_FIND_TOP4 = "SELECT a "
      + "FROM Anime a "
      + "WHERE a.statusSubmitted = '" + APPROVED_STATUS + "' "
      + "ORDER BY a.viewCount "
      + "DESC LIMIT 4";

  /**
   * Lấy danh sách top 4 phim có lượt view cao nhất.
   *
   * <p>
   * Truy vấn sử dụng native query với giới hạn là {@code TOP_LIMIT} và trạng thái
   * {@code APPROVED_STATUS}.
   * </p>
   *
   * @return danh sách {@link Anime} có lượt view cao nhất, tối đa 4 phim
   */
  @Query("SELECT a "
      + "FROM Anime a "
      + "WHERE a.statusSubmitted = '" + APPROVED_STATUS + "' "
      + "ORDER BY a.viewCount "
      + "DESC LIMIT 4")
  List<Anime> findTop4ByOrderByViewCountDesc();

  /**
   * Đếm số lượng phim theo trạng thái đã submit.
   *
   * @param status trạng thái của phim cần đếm
   * @return số lượng phim có trạng thái tương ứng
   */
  long countByStatusSubmitted(String status);
}
