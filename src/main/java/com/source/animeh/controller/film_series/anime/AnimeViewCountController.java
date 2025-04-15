package com.source.animeh.controller.film_series.anime;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.service.film_series.anime.AnimeViewCountService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý việc tăng view cho Anime khi người dùng xem xong.
 * <p>
 * Nếu người dùng chưa tăng view trong vòng 8 giờ thì sẽ được tăng thêm 1 view.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AnimeViewCountController.BASE_ENDPOINT)
public class AnimeViewCountController {

  // Base endpoint cho việc view anime
  public static final String BASE_ENDPOINT = "/public/anime-view";
  // Endpoint tăng view cho một anime
  public static final String ENDPOINT_COUNT_VIEW = "/{episodeId}/count";
  // Thông báo phản hồi
  public static final String MSG_COUNT_VIEW_SUCCESS = "Count view success!";
  AnimeViewCountService animeViewCountService;
  UserRepository userRepository;

  /**
   * Khi người dùng xem xong một Episode, phương thức này sẽ tăng view cho Anime.
   *
   * @param episodeId mã định danh của Episode
   * @return ApiResponse thông báo quá trình tăng view thành công
   */
  @PostMapping(ENDPOINT_COUNT_VIEW)
  ApiResponse<Void> countView(@PathVariable String episodeId) {
    animeViewCountService.countViewByEpisode(episodeId);
    return ApiResponse.<Void>builder()
        .message(MSG_COUNT_VIEW_SUCCESS)
        .build();
  }
}
