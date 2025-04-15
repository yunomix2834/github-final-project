package com.source.animeh.repository.film_series;

import com.source.animeh.entity.film_series.AnimeModeratorHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link AnimeModeratorHistory} dùng cho chức năng lưu trữ lịch sử
 * duyệt (moderation) của anime.
 */
public interface AnimeModeratorHistoryRepository extends
    JpaRepository<AnimeModeratorHistory, String> {

}
