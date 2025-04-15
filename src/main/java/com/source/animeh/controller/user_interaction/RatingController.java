package com.source.animeh.controller.user_interaction;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.service.user_interaction.RatingService;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các thao tác đánh giá Anime.
 * <p>
 * Phương thức trong controller này cho phép người dùng đánh giá một Anime và tính toán giá trị
 * trung bình mới của đánh giá. Các endpoint, thông báo và text quan trọng được định nghĩa dưới dạng
 * hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(RatingController.BASE_ENDPOINT)
public class RatingController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/rating";
  // --- Endpoint cho đánh giá Anime ---
  public static final String ENDPOINT_RATE_ANIME = "/anime/{animeId}";
  // --- Thông báo phản hồi ---
  public static final String MSG_RATING_SUCCESS = "Rating success, new average = ";
  RatingService ratingService;

  /**
   * Đánh giá một Anime.
   * <p>
   * Phương thức này nhận mã định danh của Anime và giá trị đánh giá, sau đó tính toán và trả về giá
   * trị trung bình mới của các đánh giá.
   * </p>
   *
   * @param animeId mã định danh của Anime
   * @param value   giá trị đánh giá được gửi từ phía người dùng
   * @return ApiResponse chứa giá trị trung bình mới sau khi cập nhật đánh giá
   */
  @PostMapping(ENDPOINT_RATE_ANIME)
  ApiResponse<BigDecimal> rateAnime(
      @PathVariable String animeId,
      @RequestParam BigDecimal value) {
    BigDecimal newAvg = ratingService.rateAnime(animeId, value);
    return ApiResponse.<BigDecimal>builder()
        .message(MSG_RATING_SUCCESS + newAvg)
        .result(newAvg)
        .build();
  }
}
