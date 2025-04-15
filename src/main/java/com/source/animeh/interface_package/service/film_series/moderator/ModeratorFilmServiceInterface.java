package com.source.animeh.interface_package.service.film_series.moderator;

import com.source.animeh.dto.request.film_series.anime.AnimeCreateRequest;
import com.source.animeh.dto.request.film_series.anime.AnimeUpdateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesCreateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesUpdateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeCreateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeUpdateRequest;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import java.io.IOException;

public interface ModeratorFilmServiceInterface {

  /**
   * Tạo Series mới (có thể kèm poster/banner).
   */
  AnimeSeriesResponse createSeries(
      AnimeSeriesCreateRequest animeSeriesCreateRequest)
      throws IOException;

  /**
   * UPDATE Series (partial)
   */
  AnimeSeriesResponse updateSeries(
      String seriesId,
      AnimeSeriesUpdateRequest animeSeriesUpdateRequest)
      throws IOException;

  void deleteSeries(
      String seriesId);

  /**
   * Tạo Anime => status_submitted = "PENDING" => folder: unapproved/<seriesId>/<animeId> => Nếu
   * seriesId=null => tự tạo series
   */
  // TODO có thể bổ sung thêm vài trường thông tin cho bộ anime
  AnimeResponse createAnimePending(
      AnimeCreateRequest animeCreateRequest)
      throws IOException;

  /**
   * UPDATE Anime (partial), nếu PENDING hoặc APPROVED
   */
  void updateAnime(
      String animeId,
      AnimeUpdateRequest animeUpdateRequest)
      throws IOException;

  // Xoá Anime
  // TODO Sau này check thêm điều kiện anime có tập thì không cho xoá hoặc có 1 phương pháp nào đấy xử lý
  void deleteAnime(
      String animeId)
      throws IOException;

  /**
   * Tạo Episode => PENDING => unapproved/<seriesId>/<animeId>/<episodeId>
   */
  // TODO đường dẫn thành biến
  EpisodeResponse createEpisode(
      EpisodeCreateRequest episodeCreateRequest)
      throws IOException, InterruptedException;

  /**
   * Sửa Episode (nếu PENDING hoặc APPROVED, không sửa video)
   */
  void updateEpisode(
      String episodeId,
      EpisodeUpdateRequest episodeUpdateRequest)
      throws IOException, InterruptedException;

  /**
   * Xoá Episode
   */
  void deleteEpisode(
      String episodeId)
      throws IOException;

  // Update Series Anime
  AnimeResponse updateAnimeSeries(
      String animeId,
      String newSeriesId);
}
