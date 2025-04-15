package com.source.animeh.service.statistic;

import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_PENDING;
import static com.source.animeh.constant.media.PredefinedType.MOVIE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.STATUS_TYPE;

import com.source.animeh.dto.response.statistic.StatisticResponse;
import com.source.animeh.entity.film_series.Type;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.AnimeTypeRepository;
import com.source.animeh.repository.film_series.TypeRepository;
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
public class AdminStatisticService {

  AnimeRepository animeRepository;
  TypeRepository typeRepository;
  AnimeTypeRepository animeTypeRepository;

  public StatisticResponse getStatisticAnimes() {

    StatisticResponse statisticResponse = new StatisticResponse();

    // Tổng anime
    statisticResponse.setTotalAnime(animeRepository.count());

    // Phim bộ
    Type tvSeries = typeRepository
        .findByNameAndType("Phim bộ", MOVIE_TYPE)
        .orElse(null);
    if (tvSeries != null) {
      long countTvSeries = animeTypeRepository
          .countByTypeId(tvSeries.getId());
      statisticResponse.setTvSeries(countTvSeries);
    }

    // Phim lẻ
    Type movie = typeRepository
        .findByNameAndType("Phim lẻ", MOVIE_TYPE)
        .orElse(null);
    if (movie != null) {
      long countMovie = animeTypeRepository
          .countByTypeId(movie.getId());
      statisticResponse.setMovie(countMovie);
    }

    // Phim đã hoàn thành
    Type finishedMovie = typeRepository
        .findByNameAndType("Đã hoàn thành", STATUS_TYPE)
        .orElse(null);
    if (finishedMovie != null) {
      long countFinishedMovie = animeTypeRepository
          .countByTypeId(finishedMovie.getId());
      statisticResponse.setFinishedMovie(countFinishedMovie);
    }

    // Phim chưa hoàn thành bao gồm phim sắp chiếu, phim đang cập nhật
    long unfinishedMovie = 0;

    // Phim sắp chiếu
    Type upcomingMovie = typeRepository
        .findByNameAndType("Sắp chiếu", STATUS_TYPE)
        .orElse(null);
    if (upcomingMovie != null) {
      unfinishedMovie += animeTypeRepository.countByTypeId(upcomingMovie.getId());
    }

    // Phim đang cập nhật
    Type updateMovie = typeRepository
        .findByNameAndType("Đang cập nhật", STATUS_TYPE)
        .orElse(null);
    if (updateMovie != null) {
      unfinishedMovie += animeTypeRepository.countByTypeId(updateMovie.getId());
    }
    statisticResponse.setUnfinishedMovie(unfinishedMovie);

    // Phim chưa duyệt
    long pendingMovie = animeRepository
        .countByStatusSubmitted(FILM_REQUEST_PENDING.getStatus());
    statisticResponse.setUnapprovedMovie(pendingMovie);

    return statisticResponse;
  }
}
