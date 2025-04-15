package com.source.animeh.repository.film_series;

import com.source.animeh.entity.film_series.AnimeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository quản lý đối tượng {@link AnimeType} dùng cho chức năng phân loại của anime.
 */
public interface AnimeTypeRepository extends JpaRepository<AnimeType, String> {

  // Hằng số cho tên parameter
  String PARAM_TYPE_ID = "typeId";

  // Hằng số truy vấn xóa các bản ghi AnimeType theo typeId
  String QUERY_DELETE_BY_TYPE_ID = "DELETE FROM AnimeType at WHERE at.type.id = :" + PARAM_TYPE_ID;

  // Hằng số truy vấn đếm số lượng bản ghi AnimeType theo typeId
  String QUERY_COUNT_BY_TYPE_ID =
      "SELECT COUNT(at) FROM AnimeType at WHERE at.type.id = :" + PARAM_TYPE_ID;

  /**
   * Kiểm tra sự tồn tại của {@link AnimeType} dựa trên animeId và typeId.
   *
   * @param animeId mã định danh của anime
   * @param typeId  mã định danh của type
   * @return {@code true} nếu tồn tại, {@code false} nếu không tồn tại
   */
  boolean existsByAnimeIdAndTypeId(String animeId, String typeId);

  /**
   * Xoá các bản ghi {@link AnimeType} dựa trên typeId.
   *
   * @param typeId mã định danh của type cần xoá
   */
  @Modifying
  @Transactional
  @Query(QUERY_DELETE_BY_TYPE_ID)
  void deleteByTypeId(@Param(PARAM_TYPE_ID) String typeId);

  /**
   * Đếm số lượng bản ghi {@link AnimeType} dựa trên typeId.
   *
   * @param typeId mã định danh của type
   * @return số lượng bản ghi có typeId tương ứng
   */
  @Query(QUERY_COUNT_BY_TYPE_ID)
  long countByTypeId(@Param(PARAM_TYPE_ID) String typeId);
}
