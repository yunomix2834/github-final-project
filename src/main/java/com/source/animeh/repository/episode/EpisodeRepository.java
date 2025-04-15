package com.source.animeh.repository.episode;

import com.source.animeh.entity.episode.Episode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý thực thể {@link Episode} và hỗ trợ các truy vấn động.
 */
public interface EpisodeRepository extends JpaRepository<Episode, String>,
    JpaSpecificationExecutor<Episode> {

  // Hằng số cho tên tham số trong truy vấn
  String PARAM_ANIME_ID = "animeId";

  // Hằng số JPQL truy vấn đếm số lượng Episode theo animeId
  String JPQL_COUNT_EPISODES_BY_ANIMEID =
      "SELECT COUNT(e) FROM Episode e WHERE e.anime.id = :" + PARAM_ANIME_ID;

  /**
   * Tìm danh sách {@link Episode} dựa trên mã định danh của anime.
   *
   * @param animeId mã định danh của anime
   * @return danh sách {@link Episode} có liên quan đến anime được chỉ định
   */
  List<Episode> findByAnimeId(String animeId);

  /**
   * Đếm số lượng {@link Episode} dựa trên mã định danh của anime.
   *
   * @param animeId mã định danh của anime
   * @return số lượng {@link Episode} được tìm thấy
   */
  @Query(JPQL_COUNT_EPISODES_BY_ANIMEID)
  int countByAnimeId(@Param(PARAM_ANIME_ID) String animeId);

  /**
   * Đếm số lượng {@link Episode} dựa trên mã định danh của anime và trạng thái của episode.
   *
   * @param animeId mã định danh của anime
   * @param status  trạng thái của episode
   * @return số lượng {@link Episode} thỏa mãn điều kiện
   */
  int countByAnimeIdAndStatus(String animeId, String status);
}
