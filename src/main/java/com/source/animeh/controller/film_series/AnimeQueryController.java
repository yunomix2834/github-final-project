package com.source.animeh.controller.film_series;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import com.source.animeh.service.film_series.AnimeQueryService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác truy vấn Anime, Series và Episode.
 * <p>
 * Các thao tác bao gồm: lấy danh sách tất cả Series, lấy thông tin chi tiết của Series, lấy danh
 * sách Anime trong Series, lấy danh sách Anime, lấy thông tin Anime, lấy danh sách Episodes của
 * Anime và lấy thông tin Episode. Các endpoint, thông báo và default value được định nghĩa dưới
 * dạng hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AnimeQueryController.BASE_ENDPOINT)
public class AnimeQueryController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/animes-query";
  // --- Endpoint cho Series ---
  public static final String ENDPOINT_GET_ALL_SERIES = "/series";
  public static final String ENDPOINT_GET_SERIES_BY_ID = "/series/{id}";
  public static final String ENDPOINT_GET_ANIMES_IN_SERIES = "/series/{seriesId}/animes";
  // --- Endpoint cho Anime ---
  public static final String ENDPOINT_GET_ALL_ANIME = "/animes";
  public static final String ENDPOINT_GET_ANIME_BY_ID = "/anime/{id}";
  // --- Endpoint cho Episode ---
  public static final String ENDPOINT_GET_EPISODES_OF_ANIME = "/anime/{animeId}/episodes";
  public static final String ENDPOINT_GET_EPISODE_BY_ID = "/episode/{episodeId}";
  // --- Thông báo phản hồi ---
  public static final String MSG_GET_ALL_SERIES_SUCCESS = "Get all anime series successfully!";
  public static final String MSG_GET_SERIES_SUCCESS = "Get series successfully!";
  public static final String MSG_GET_ANIMES_IN_SERIES_SUCCESS = "Get anime series successfully!";
  public static final String MSG_GET_ALL_ANIME_SUCCESS = "Get all anime successfully!";
  public static final String MSG_GET_ANIME_SUCCESS = "Get anime successfully!";
  public static final String MSG_GET_EPISODES_SUCCESS = "Get episode successfully!";
  public static final String MSG_GET_EPISODE_SUCCESS = "Get episode successfully!";
  // --- Default values ---
  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 10;
  AnimeQueryService animeQueryService;

  /**
   * Lấy danh sách tất cả các Series của Anime.
   *
   * @return ApiResponse chứa danh sách các Anime Series
   */
  @GetMapping(ENDPOINT_GET_ALL_SERIES)
  ApiResponse<List<AnimeSeriesResponse>> getAllSeries() {
    List<AnimeSeriesResponse> result = animeQueryService.getAllSeries();
    return ApiResponse.<List<AnimeSeriesResponse>>builder()
        .message(MSG_GET_ALL_SERIES_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy thông tin của một Series theo ID.
   *
   * @param id mã định danh của Series
   * @return ApiResponse chứa thông tin chi tiết của Series
   */
  @GetMapping(ENDPOINT_GET_SERIES_BY_ID)
  ApiResponse<AnimeSeriesResponse> getSeriesById(@PathVariable("id") String id) {
    AnimeSeriesResponse result = animeQueryService.getSeriesById(id);
    return ApiResponse.<AnimeSeriesResponse>builder()
        .message(MSG_GET_SERIES_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách Anime thuộc một Series.
   *
   * @param seriesId mã định danh của Series
   * @return ApiResponse chứa danh sách Anime của Series
   */
  @GetMapping(ENDPOINT_GET_ANIMES_IN_SERIES)
  ApiResponse<List<AnimeResponse>> getAnimesInSeries(
      @PathVariable("seriesId") String seriesId) {
    List<AnimeResponse> result = animeQueryService.getAnimesInSeries(seriesId);
    return ApiResponse.<List<AnimeResponse>>builder()
        .message(MSG_GET_ANIMES_IN_SERIES_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách tất cả Anime với phân trang.
   *
   * @param page số trang (mặc định là 0)
   * @param size kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách Anime dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_ALL_ANIME)
  ApiResponse<Page<AnimeResponse>> getAllAnime(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<AnimeResponse> result = animeQueryService.getAllAnime(page, size);
    return ApiResponse.<Page<AnimeResponse>>builder()
        .message(MSG_GET_ALL_ANIME_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy thông tin của một Anime theo ID.
   *
   * @param id mã định danh của Anime
   * @return ApiResponse chứa thông tin chi tiết của Anime
   */
  @GetMapping(ENDPOINT_GET_ANIME_BY_ID)
  ApiResponse<AnimeResponse> getAnimeById(@PathVariable("id") String id) {
    AnimeResponse result = animeQueryService.getAnimeById(id);
    return ApiResponse.<AnimeResponse>builder()
        .message(MSG_GET_ANIME_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách các Episodes của một Anime.
   *
   * @param animeId mã định danh của Anime
   * @return ApiResponse chứa danh sách Episodes của Anime
   */
  @GetMapping(ENDPOINT_GET_EPISODES_OF_ANIME)
  ApiResponse<List<EpisodeResponse>> getEpisodesOfAnime(
      @PathVariable("animeId") String animeId) {
    List<EpisodeResponse> result = animeQueryService.getEpisodesOfAnime(animeId);
    return ApiResponse.<List<EpisodeResponse>>builder()
        .message(MSG_GET_EPISODES_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy thông tin của một Episode theo ID.
   *
   * @param episodeId mã định danh của Episode
   * @return ApiResponse chứa thông tin chi tiết của Episode
   */
  @GetMapping(ENDPOINT_GET_EPISODE_BY_ID)
  ApiResponse<EpisodeResponse> getEpisode(@PathVariable("episodeId") String episodeId) {
    EpisodeResponse result = animeQueryService.getEpisodeById(episodeId);
    return ApiResponse.<EpisodeResponse>builder()
        .message(MSG_GET_EPISODE_SUCCESS)
        .result(result)
        .build();
  }
}
