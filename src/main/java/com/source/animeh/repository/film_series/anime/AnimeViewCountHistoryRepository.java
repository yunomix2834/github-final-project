package com.source.animeh.repository.film_series.anime;

import com.source.animeh.entity.film_series.anime.AnimeViewCountHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link AnimeViewCountHistory} dùng để lưu trữ lịch sử lượt xem của
 * anime theo người dùng.
 */
public interface AnimeViewCountHistoryRepository extends
    JpaRepository<AnimeViewCountHistory, String> {

  Optional<AnimeViewCountHistory> findByAnimeId(String animeId);
}
