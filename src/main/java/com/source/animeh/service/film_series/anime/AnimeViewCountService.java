package com.source.animeh.service.film_series.anime;

import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.anime.AnimeViewCountHistory;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.anime.AnimeViewCountHistoryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnimeViewCountService {

  AnimeRepository animeRepository;
  UserRepository userRepository;
  EpisodeRepository episodeRepository;
  AnimeViewCountHistoryRepository animeCountHistoryRepository;

  /**
   * Tính viewcount cho Anime dựa trên việc người dùng xem một Episode cụ thể.
   * <p>
   * Mỗi lần được gọi, phương thức sẽ:
   * <ul>
   *   <li>Tìm Anime tương ứng với episodeId</li>
   *   <li>Cộng +1 vào bảng AnimeViewCountHistory (nếu chưa có record thì tạo)</li>
   *   <li>Đồng bộ anime.setViewCount(anime.getViewCount()+1)</li>
   * </ul>
   * </p>
   *
   * @param episodeId mã định danh của tập phim
   * @throws AppException nếu Episode hoặc Anime không tồn tại
   */
  public void countViewByEpisode(String episodeId) {

    Episode episode = episodeRepository
        .findById(episodeId)
        .orElseThrow(() -> new AppException(ErrorCode.EPISODE_NOT_FOUND));

    Anime anime = episode.getAnime();
    if (anime == null) {
      throw new AppException(ErrorCode.ANIME_NOT_FOUND);
    }

    // Lấy ra record trong AnimeViewCountHistory
    Optional<AnimeViewCountHistory> opt = animeCountHistoryRepository
        .findByAnimeId(anime.getId());

    AnimeViewCountHistory record;
    if (opt.isEmpty()) {
      // Chưa có => tạo
      record = new AnimeViewCountHistory();
      record.setId(UUID.randomUUID().toString());
      record.setAnime(anime);
      record.setTotalView(0L);
      record.setCreatedAt(LocalDateTime.now());
    } else {
      record = opt.get();
    }
    // +1 view
    long current = (record.getTotalView() == null ? 0 : record.getTotalView());
    record.setTotalView(current + 1);
    record.setUpdatedAt(LocalDateTime.now());
    animeCountHistoryRepository.save(record);

    // Đồng bộ sang anime.viewCount
    long oldCount = (anime.getViewCount() == null) ? 0 : anime.getViewCount();
    anime.setViewCount(oldCount + 1);
    animeRepository.save(anime);

    log.info("Episode {} => +1 view anime {}, totalView = {}",
        episodeId, anime.getId(), anime.getViewCount());
  }
}

