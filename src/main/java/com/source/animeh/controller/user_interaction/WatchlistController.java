package com.source.animeh.controller.user_interaction;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.user_interaction.LocalWatchlistRequest;
import com.source.animeh.dto.response.user_interaction.WatchlistResponse;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.service.user_interaction.WatchlistService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý danh sách theo dõi (watchlist) của Anime.
 * <p>
 * Các thao tác bao gồm: thêm Anime vào watchlist, xoá Anime khỏi watchlist, lấy watchlist từ DB
 * hoặc localStorage, cũng như đồng bộ watchlist từ localStorage sang DB. Các endpoint, thông báo và
 * default value được định nghĩa dưới dạng hằng số để dễ bảo trì và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class WatchlistController {

  // --- Endpoint Paths ---
  public static final String ENDPOINT_ADD_WATCHLIST = "/watchlist/add";
  public static final String ENDPOINT_REMOVE_WATCHLIST = "/watchlist/remove";
  public static final String ENDPOINT_GET_WATCHLIST = "/watchlist";
  public static final String ENDPOINT_GET_LOCAL_WATCHLIST = "/public/watchlist";
  public static final String ENDPOINT_SYNC_WATCHLIST = "/watchlist/sync";
  // --- Thông báo phản hồi ---
  public static final String MSG_ADD_WATCHLIST_SUCCESS = "Added anime to watchlist successfully!";
  public static final String MSG_REMOVE_WATCHLIST_SUCCESS = "Removed anime from watchlist successfully!";
  public static final String MSG_GET_WATCHLIST_SUCCESS = "Get all anime from watchlist successfully!";
  public static final String MSG_SYNC_WATCHLIST_SUCCESS = "Sync watchlist successfully!";
  // --- Default values for pagination ---
  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 10;
  WatchlistService watchlistService;
  UserRepository userRepository;

  /**
   * Thêm Anime vào watchlist.
   * <p>
   * (1) Nếu người dùng chưa đăng nhập thì lưu vào localStorage (không cần gọi API). (2) Khi người
   * dùng đã đăng nhập thì gọi API để lưu vào DB.
   * </p>
   *
   * @param animeId mã định danh của Anime cần thêm vào watchlist
   * @return ApiResponse thông báo quá trình thêm thành công
   */
  @PostMapping(ENDPOINT_ADD_WATCHLIST)
  ApiResponse<Void> addAnimeToWatchlist(@RequestParam String animeId) {
    watchlistService.addAnimeToWatchlist(animeId);
    return ApiResponse.<Void>builder()
        .message(MSG_ADD_WATCHLIST_SUCCESS)
        .build();
  }

  /**
   * Xoá Anime khỏi watchlist.
   *
   * @param animeId mã định danh của Anime cần xoá khỏi watchlist
   * @return ApiResponse thông báo quá trình xoá thành công
   */
  @DeleteMapping(ENDPOINT_REMOVE_WATCHLIST)
  ApiResponse<Void> removeAnimeFromWatchlist(@RequestParam String animeId) {
    watchlistService.removeAnimeFromWatchlist(animeId);
    return ApiResponse.<Void>builder()
        .message(MSG_REMOVE_WATCHLIST_SUCCESS)
        .build();
  }

  /**
   * Lấy danh sách watchlist từ DB.
   *
   * @param page số trang (mặc định là 0)
   * @param size kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách Anime trong watchlist dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_WATCHLIST)
  ApiResponse<Page<WatchlistResponse>> getWatchlist(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<WatchlistResponse> result = watchlistService.getUserWatchlist(page, size);
    return ApiResponse.<Page<WatchlistResponse>>builder()
        .message(MSG_GET_WATCHLIST_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách watchlist từ localStorage (dạng "ảo").
   *
   * @param request yêu cầu chứa danh sách localAnimeIds
   * @param page    số trang (mặc định là 0)
   * @param size    kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách Anime trong watchlist dạng phân trang
   */
  @PostMapping(ENDPOINT_GET_LOCAL_WATCHLIST)
  ApiResponse<Page<WatchlistResponse>> getLocalWatchlist(
      @RequestBody LocalWatchlistRequest request,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<WatchlistResponse> result = watchlistService.getLocalWatchlist(request, page, size);
    return ApiResponse.<Page<WatchlistResponse>>builder()
        .message(MSG_GET_WATCHLIST_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Đồng bộ watchlist từ localStorage sang DB.
   * <p>
   * Khi người dùng đã có watchlist lưu cục bộ trước đó (localAnimeIds), sau khi đăng nhập, người
   * dùng có thể gọi API này để đồng bộ watchlist lên DB.
   * </p>
   *
   * @param localWatchlistRequest yêu cầu chứa thông tin watchlist từ localStorage
   * @return ApiResponse thông báo quá trình đồng bộ thành công
   */
  @PostMapping(ENDPOINT_SYNC_WATCHLIST)
  ApiResponse<Void> syncLocalWatchlist(
      @RequestBody LocalWatchlistRequest localWatchlistRequest) {
    watchlistService.syncLocalWatchlist(localWatchlistRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_SYNC_WATCHLIST_SUCCESS)
        .build();
  }
}
