package com.source.animeh.repository.film_series.schedule;

import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository quản lý đối tượng {@link PublishingSchedule} dùng cho chức năng lưu trữ lịch phát hành
 * của anime.
 */
public interface PublishingScheduleRepository extends JpaRepository<PublishingSchedule, String>,
    JpaSpecificationExecutor<PublishingSchedule> {

  /**
   * Tìm kiếm {@link PublishingSchedule} dựa trên mã định danh của anime.
   *
   * @param animeId mã định danh của anime.
   * @return {@code Optional<PublishingSchedule>} chứa đối tượng nếu tìm thấy, hoặc rỗng nếu không
   * tồn tại.
   */
  Optional<PublishingSchedule> findByAnimeId(String animeId);

  Boolean existsByAnimeId(String animeId);

  void deleteByAnimeId(String animeId);

}
