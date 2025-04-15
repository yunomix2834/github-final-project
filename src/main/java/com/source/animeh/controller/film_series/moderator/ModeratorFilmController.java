package com.source.animeh.controller.film_series.moderator;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.film_series.anime.AnimeCreateRequest;
import com.source.animeh.dto.request.film_series.anime.AnimeUpdateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesCreateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesUpdateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeCreateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeUpdateRequest;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import com.source.animeh.service.film_series.moderator.ModeratorFilmService;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác của Moderator liên quan đến Anime, Series và Episode.
 * <p>
 * Các thao tác bao gồm: tạo, cập nhật (partial), xoá và chuyển trạng thái pending cho
 * Anime/Episode, cũng như thay đổi Series của Anime. Các endpoint và thông báo được định nghĩa dưới
 * dạng hằng số để dễ quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(ModeratorFilmController.BASE_ENDPOINT)
class ModeratorFilmController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/moderator-film";
  // --- Endpoints cho Series ---
  public static final String ENDPOINT_CREATE_SERIES = "/series";
  public static final String ENDPOINT_UPDATE_SERIES = "/series/{seriesId}";
  public static final String ENDPOINT_DELETE_SERIES = "/series/{seriesId}";
  // --- Endpoints cho Anime ---
  public static final String ENDPOINT_CREATE_ANIME = "/anime";
  public static final String ENDPOINT_UPDATE_ANIME = "/anime/{animeId}";
  public static final String ENDPOINT_REVERT_ANIME_PENDING = "/anime/{animeId}/revert-pending";
  public static final String ENDPOINT_DELETE_ANIME = "/anime/{animeId}";
  public static final String ENDPOINT_UPDATE_ANIME_SERIES = "/anime/{animeId}/change-series";
  // --- Endpoints cho Episode ---
  public static final String ENDPOINT_CREATE_EPISODE = "/episode";
  public static final String ENDPOINT_UPDATE_EPISODE = "/episode/{episodeId}";
  public static final String ENDPOINT_REVERT_EPISODE_PENDING = "/episode/{episodeId}/revert-pending";
  public static final String ENDPOINT_DELETE_EPISODE = "/episode/{episodeId}";
  // --- Thông báo phản hồi ---
  // Thông báo cho Series
  public static final String MSG_CREATED_SERIES = "Created series!";
  public static final String MSG_UPDATED_SERIES_PARTIALLY = "Updated series partially!";
  public static final String MSG_DELETED_SERIES = "Deleted series!";
  // Thông báo cho Anime
  public static final String MSG_CREATED_ANIME_PENDING = "Created anime pending!";
  public static final String MSG_UPDATED_ANIME_PARTIALLY = "Updated anime partially!";
  public static final String MSG_REVERTED_ANIME_PENDING = "Reverted anime from rejected to pending successfully!";
  public static final String MSG_DELETED_ANIME_PENDING = "Deleted anime pending!";
  public static final String MSG_UPDATED_ANIME_SERIES = "Update anime's series successfully!";
  // Thông báo cho Episode
  public static final String MSG_CREATED_EPISODE = "Created episode!";
  public static final String MSG_UPDATED_EPISODE_PARTIALLY = "Updated episode partially!";
  public static final String MSG_REVERTED_EPISODE_PENDING = "Reverted episode from rejected to pending successfully!";
  public static final String MSG_DELETED_EPISODE_PENDING = "Deleted episode pending!";
  ModeratorFilmService moderatorFilmService;

  // --- Phương thức Series ---

  /**
   * Tạo mới một Series.
   * <p>
   * Dữ liệu được nhận thông qua @ModelAttribute để hỗ trợ nhận MultipartFile nếu cần.
   * </p>
   *
   * @param req yêu cầu tạo Series
   * @return ApiResponse chứa thông tin Series được tạo
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @PostMapping(ENDPOINT_CREATE_SERIES)
  ApiResponse<AnimeSeriesResponse> createSeries(@ModelAttribute AnimeSeriesCreateRequest req)
      throws IOException {
    AnimeSeriesResponse response = moderatorFilmService.createSeries(req);
    return ApiResponse.<AnimeSeriesResponse>builder()
        .message(MSG_CREATED_SERIES)
        .result(response)
        .build();
  }

  /**
   * Cập nhật thông tin (partial) của một Series.
   *
   * @param seriesId mã định danh của Series cần cập nhật
   * @param req      yêu cầu cập nhật Series
   * @return ApiResponse chứa thông tin Series sau khi cập nhật
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @PatchMapping(ENDPOINT_UPDATE_SERIES)
  ApiResponse<AnimeSeriesResponse> updateSeries(@PathVariable String seriesId,
      @ModelAttribute AnimeSeriesUpdateRequest req)
      throws IOException {
    AnimeSeriesResponse response = moderatorFilmService.updateSeries(seriesId, req);
    return ApiResponse.<AnimeSeriesResponse>builder()
        .message(MSG_UPDATED_SERIES_PARTIALLY)
        .result(response)
        .build();
  }

  /**
   * Xoá một Series.
   *
   * @param seriesId mã định danh của Series cần xoá
   * @return ApiResponse thông báo quá trình xoá Series thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @DeleteMapping(ENDPOINT_DELETE_SERIES)
  ApiResponse<Void> deleteSeries(@PathVariable String seriesId)
      throws IOException {
    moderatorFilmService.deleteSeries(seriesId);
    return ApiResponse.<Void>builder()
        .message(MSG_DELETED_SERIES)
        .build();
  }

  // --- Phương thức Anime ---

  /**
   * Tạo mới một Anime (trạng thái pending).
   *
   * @param req yêu cầu tạo Anime
   * @return ApiResponse chứa thông tin Anime được tạo
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @PostMapping(ENDPOINT_CREATE_ANIME)
  ApiResponse<AnimeResponse> createAnime(@ModelAttribute AnimeCreateRequest req)
      throws IOException {
    AnimeResponse response = moderatorFilmService.createAnimePending(req);
    return ApiResponse.<AnimeResponse>builder()
        .message(MSG_CREATED_ANIME_PENDING)
        .result(response)
        .build();
  }

  /**
   * Cập nhật thông tin (partial) của một Anime.
   * <p>
   * Cập nhật các trường như tiêu đề, mô tả.
   * </p>
   *
   * @param animeId mã định danh của Anime cần cập nhật
   * @param req     yêu cầu cập nhật Anime
   * @return ApiResponse thông báo quá trình cập nhật thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @PatchMapping(ENDPOINT_UPDATE_ANIME)
  ApiResponse<Void> updateAnime(@PathVariable String animeId,
      @ModelAttribute AnimeUpdateRequest req)
      throws IOException {
    moderatorFilmService.updateAnime(animeId, req);
    return ApiResponse.<Void>builder()
        .message(MSG_UPDATED_ANIME_PARTIALLY)
        .build();
  }

  /**
   * Chuyển trạng thái của Anime từ rejected sang pending.
   *
   * @param animeId mã định danh của Anime cần chuyển trạng thái
   * @return ApiResponse thông báo quá trình chuyển trạng thái thành công
   */
  @PatchMapping(ENDPOINT_REVERT_ANIME_PENDING)
  ApiResponse<Void> revertAnimeToPending(@PathVariable String animeId) {
    moderatorFilmService.revertAnimeToPending(animeId);
    return ApiResponse.<Void>builder()
        .message(MSG_REVERTED_ANIME_PENDING)
        .build();
  }

  /**
   * Xoá một Anime (trạng thái pending).
   *
   * @param animeId mã định danh của Anime cần xoá
   * @return ApiResponse thông báo quá trình xoá Anime thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @DeleteMapping(ENDPOINT_DELETE_ANIME)
  ApiResponse<Void> deleteAnime(@PathVariable("animeId") String animeId)
      throws IOException {
    moderatorFilmService.deleteAnime(animeId);
    return ApiResponse.<Void>builder()
        .message(MSG_DELETED_ANIME_PENDING)
        .build();
  }

  /**
   * Cập nhật Series cho Anime.
   *
   * @param animeId     mã định danh của Anime cần cập nhật
   * @param newSeriesId mã định danh Series mới được gán cho Anime
   * @return ApiResponse chứa thông tin Anime sau khi cập nhật Series
   */
  @PatchMapping(ENDPOINT_UPDATE_ANIME_SERIES)
  ApiResponse<AnimeResponse> updateAnimeSeries(@PathVariable("animeId") String animeId,
      @RequestParam String newSeriesId) {
    AnimeResponse response = moderatorFilmService.updateAnimeSeries(animeId, newSeriesId);
    return ApiResponse.<AnimeResponse>builder()
        .message(MSG_UPDATED_ANIME_SERIES)
        .result(response)
        .build();
  }

  // --- Phương thức Episode ---

  /**
   * Tạo mới một Episode.
   *
   * @param req yêu cầu tạo Episode
   * @return ApiResponse chứa thông tin Episode được tạo
   * @throws IOException          nếu có lỗi trong quá trình xử lý
   * @throws InterruptedException nếu quá trình xử lý bị gián đoạn
   */
  @PostMapping(ENDPOINT_CREATE_EPISODE)
  ApiResponse<EpisodeResponse> createEpisode(@ModelAttribute EpisodeCreateRequest req)
      throws IOException, InterruptedException {
    EpisodeResponse response = moderatorFilmService.createEpisode(req);
    return ApiResponse.<EpisodeResponse>builder()
        .message(MSG_CREATED_EPISODE)
        .result(response)
        .build();
  }

  /**
   * Cập nhật thông tin (partial) của một Episode.
   *
   * @param episodeId mã định danh của Episode cần cập nhật
   * @param req       yêu cầu cập nhật Episode
   * @return ApiResponse thông báo quá trình cập nhật Episode thành công
   * @throws IOException          nếu có lỗi trong quá trình xử lý
   * @throws InterruptedException nếu quá trình xử lý bị gián đoạn
   */
  @PatchMapping(ENDPOINT_UPDATE_EPISODE)
  ApiResponse<Void> updateEpisode(@PathVariable String episodeId,
      @ModelAttribute EpisodeUpdateRequest req)
      throws IOException, InterruptedException {
    moderatorFilmService.updateEpisode(episodeId, req);
    return ApiResponse.<Void>builder()
        .message(MSG_UPDATED_EPISODE_PARTIALLY)
        .build();
  }

  /**
   * Chuyển trạng thái của Episode từ rejected sang pending.
   *
   * @param episodeId mã định danh của Episode cần chuyển trạng thái
   * @return ApiResponse thông báo quá trình chuyển trạng thái thành công
   */
  @PatchMapping(ENDPOINT_REVERT_EPISODE_PENDING)
  ApiResponse<Void> revertEpisodeToPending(@PathVariable String episodeId) {
    moderatorFilmService.revertEpisodeToPending(episodeId);
    return ApiResponse.<Void>builder()
        .message(MSG_REVERTED_EPISODE_PENDING)
        .build();
  }

  /**
   * Xoá một Episode (trạng thái pending).
   *
   * @param episodeId mã định danh của Episode cần xoá
   * @return ApiResponse thông báo quá trình xoá Episode thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý
   */
  @DeleteMapping(ENDPOINT_DELETE_EPISODE)
  ApiResponse<Void> deleteEpisode(@PathVariable("episodeId") String episodeId)
      throws IOException {
    moderatorFilmService.deleteEpisode(episodeId);
    return ApiResponse.<Void>builder()
        .message(MSG_DELETED_EPISODE_PENDING)
        .build();
  }
}
