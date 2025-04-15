package com.source.animeh.controller.film_series;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.filter.AnimeFilterRequest;
import com.source.animeh.dto.request.filter.EpisodeFilterRequest;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.service.film_series.AnimeService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác truy vấn Anime.
 * <p>
 * Các thao tác bao gồm: lấy top 4 Anime có lượt view cao nhất và lọc Anime theo tiêu chí nâng cao.
 * Các endpoint, thông báo và default value được định nghĩa dưới dạng hằng số để dễ quản lý và thay
 * đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AnimeController.BASE_ENDPOINT)
public class AnimeController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/animes-query";
  // --- Endpoint ---
  public static final String ENDPOINT_TOP4 = "/top4";
  public static final String ENDPOINT_FILTER_ADVANCED = "/filter-advanced";
  // --- Thông báo phản hồi ---
  public static final String MSG_GET_TOP4_SUCCESS = "Get Top 4 Animes Successfully!";
  public static final String MSG_FILTER_SUCCESS = "Filter Animes Successfully!";
  // --- Default values ---
  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 10;
  AnimeService animeService;

  /**
   * Lấy danh sách 4 Anime có lượt view cao nhất.
   *
   * @return ApiResponse chứa danh sách Anime (top 4) có lượt view cao nhất
   */
  @GetMapping(ENDPOINT_TOP4)
  ApiResponse<List<AnimeResponse>> getTop4Animes() {
    List<AnimeResponse> result = animeService.getTop4Animes();
    return ApiResponse.<List<AnimeResponse>>builder()
        .message(MSG_GET_TOP4_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lọc Anime theo tiêu chí nâng cao.
   * <p>
   * Tiêu chí lọc bao gồm: danh sách typeId (tagIds), năm phát hành, rating tối thiểu, nominated
   * (true/false).
   * </p>
   *
   * @param animeFilterRequest yêu cầu lọc Anime nâng cao
   * @param page               số trang (mặc định là 0)
   * @param size               kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách Anime được lọc dạng phân trang
   */
  @PostMapping(ENDPOINT_FILTER_ADVANCED)
  ApiResponse<Page<AnimeResponse>> filterAnimeAdvanced(
      @RequestBody AnimeFilterRequest animeFilterRequest,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<AnimeResponse> result = animeService.filterAnimeAdvanced(animeFilterRequest, page, size);
    return ApiResponse.<Page<AnimeResponse>>builder()
        .message(MSG_FILTER_SUCCESS)
        .result(result)
        .build();
  }

  @PostMapping("/episodes/filter-advanced")
  ApiResponse<Page<EpisodeResponse>> filterEpisodeAdvanced(
      @RequestBody EpisodeFilterRequest episodeFilterRequest,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<EpisodeResponse> result = animeService.filterEpisodeAdvanced(episodeFilterRequest, page,
        size);
    return ApiResponse.<Page<EpisodeResponse>>builder()
        .message("Filter Episodes Successfully!")
        .result(result)
        .build();
  }

  @GetMapping("/random")
  public ApiResponse<AnimeResponse> getRandomAnime() {
    AnimeResponse randomAnime = animeService.getRandomApprovedAnime();
    return ApiResponse.<AnimeResponse>builder()
        .message("Get random Anime successfully!")
        .result(randomAnime)
        .build();
  }
}
