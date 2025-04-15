package com.source.animeh.controller.film_series.moderator;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.service.film_series.moderator.AdminFilmService;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các thao tác duyệt/reject Anime và Episode của Moderator.
 * <p>
 * Các endpoint và thông báo được định nghĩa thành hằng số để dễ dàng quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AdminFilmController.BASE_ENDPOINT)
public class AdminFilmController {

  // Base endpoint
  public static final String BASE_ENDPOINT = "/admin-film";
  // Endpoint cho Anime
  public static final String ENDPOINT_APPROVE_ANIME = "/approve-anime/{animeId}";
  public static final String ENDPOINT_REJECT_ANIME = "/reject-anime/{animeId}";
  // Endpoint cho Episode
  public static final String ENDPOINT_APPROVE_EPISODE = "/approve-episode/{episodeId}";
  public static final String ENDPOINT_REJECT_EPISODE = "/reject-episode/{episodeId}";
  // Thông báo phản hồi
  public static final String MSG_APPROVE_ANIME_SUCCESS = "Approved anime successfully!";
  public static final String MSG_REJECT_ANIME_SUCCESS = "Rejected anime successfully!";
  public static final String MSG_APPROVE_EPISODE_SUCCESS = "Approved episode successfully!";
  public static final String MSG_REJECT_EPISODE_SUCCESS = "Rejected episode successfully!";
  AdminFilmService adminFilmService;

  /**
   * Duyệt Anime: di chuyển folder và thiết lập trạng thái APPROVED.
   *
   * @param animeId mã định danh của Anime cần duyệt
   * @return ApiResponse thông báo quá trình duyệt thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý file
   */
  @PostMapping(ENDPOINT_APPROVE_ANIME)
  ApiResponse<Void> approveAnime(@PathVariable("animeId") String animeId)
      throws IOException {
    adminFilmService.approveAnime(animeId);
    return ApiResponse.<Void>builder()
        .message(MSG_APPROVE_ANIME_SUCCESS)
        .build();
  }

  /**
   * Reject Anime: thiết lập trạng thái REJECTED và ghi nhận lý do từ phía Admin.
   *
   * @param animeId mã định danh của Anime cần từ chối
   * @param reason  lý do từ chối
   * @return ApiResponse thông báo quá trình từ chối thành công
   */
  @PostMapping(ENDPOINT_REJECT_ANIME)
  ApiResponse<Void> rejectAnime(@PathVariable("animeId") String animeId,
      @RequestBody String reason) {
    adminFilmService.rejectAnime(animeId, reason);
    return ApiResponse.<Void>builder()
        .message(MSG_REJECT_ANIME_SUCCESS)
        .build();
  }

  /**
   * Duyệt Episode: di chuyển folder và thiết lập trạng thái APPROVED.
   *
   * @param episodeId mã định danh của Episode cần duyệt
   * @return ApiResponse thông báo quá trình duyệt thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý file
   */
  @PostMapping(ENDPOINT_APPROVE_EPISODE)
  ApiResponse<Void> approveEpisode(@PathVariable("episodeId") String episodeId)
      throws IOException {
    adminFilmService.approveEpisode(episodeId);
    return ApiResponse.<Void>builder()
        .message(MSG_APPROVE_EPISODE_SUCCESS)
        .build();
  }

  /**
   * Reject Episode: thiết lập trạng thái REJECTED và ghi nhận lý do từ phía Admin.
   *
   * @param episodeId mã định danh của Episode cần từ chối
   * @param reason    lý do từ chối
   * @return ApiResponse thông báo quá trình từ chối thành công
   */
  @PostMapping(ENDPOINT_REJECT_EPISODE)
  ApiResponse<Void> rejectEpisode(@PathVariable("episodeId") String episodeId,
      @RequestBody String reason) {
    adminFilmService.rejectEpisode(episodeId, reason);
    return ApiResponse.<Void>builder()
        .message(MSG_REJECT_EPISODE_SUCCESS)
        .build();
  }
}
