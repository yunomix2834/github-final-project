package com.source.animeh.controller.user_interaction;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.user_interaction.LocalHistoryRequest;
import com.source.animeh.dto.response.user_interaction.ViewingHistoryResponse;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.service.user_interaction.ViewingHistoryService;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý lịch sử xem của người dùng.
 * <p>
 * Các thao tác bao gồm: thêm lịch sử xem, lấy lịch sử xem, xoá lịch sử xem và đồng bộ lịch sử từ
 * localStorage sang DB. Các endpoint, thông báo và default value được định nghĩa dưới dạng hằng số
 * để dễ bảo trì và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class ViewingHistoryController {

  // --- Endpoint Paths ---
  public static final String ENDPOINT_ADD_HISTORY = "/history/add";
  public static final String ENDPOINT_GET_HISTORY = "/history";
  public static final String ENDPOINT_GET_LOCAL_HISTORY = "/public/history";
  public static final String ENDPOINT_DELETE_HISTORY = "/history/delete";
  public static final String ENDPOINT_SYNC_HISTORY = "/history/sync";
  // --- Thông báo phản hồi ---
  public static final String MSG_HISTORY_ADDED = "Added view history successfully!";
  public static final String MSG_HISTORY_FETCHED = "Get view history successfully!";
  public static final String MSG_HISTORY_DELETED = "Deleted view history successfully!";
  public static final String MSG_HISTORY_SYNCED = "Sync local history successfully!";
  // --- Default values for pagination ---
  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 10;
  ViewingHistoryService viewingHistoryService;
  UserRepository userRepository;

  /**
   * Thêm lịch sử xem của một tập phim.
   * <p>
   * Nếu người dùng chưa login thì lưu vào localStorage (không cần API), còn khi đã login thì lưu
   * vào DB.
   * </p>
   *
   * @param episodeId       mã định danh của tập phim
   * @param watchedDuration thời lượng đã xem (có thể null)
   * @return ApiResponse thông báo quá trình thêm lịch sử thành công
   */
  @PostMapping(ENDPOINT_ADD_HISTORY)
  ApiResponse<Void> addViewHistory(@RequestParam String episodeId,
      @RequestParam(required = false) BigDecimal watchedDuration) {
    viewingHistoryService.addViewHistory(episodeId, watchedDuration);
    return ApiResponse.<Void>builder()
        .message(MSG_HISTORY_ADDED)
        .build();
  }

  /**
   * Lấy danh sách lịch sử xem của người dùng từ DB.
   *
   * @param page số trang (mặc định là 0)
   * @param size kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách lịch sử xem dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_HISTORY)
  ApiResponse<Page<ViewingHistoryResponse>> getViewHistory(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<ViewingHistoryResponse> result = viewingHistoryService.getAllHistory(page, size);
    return ApiResponse.<Page<ViewingHistoryResponse>>builder()
        .message(MSG_HISTORY_FETCHED)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách lịch sử xem từ localStorage (có phân trang).
   *
   * @param localHistoryRequest yêu cầu chứa thông tin lịch sử xem lưu cục bộ
   * @param page                số trang (mặc định là 0)
   * @param size                kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách lịch sử xem dạng phân trang
   */
  @PostMapping(ENDPOINT_GET_LOCAL_HISTORY)
  ApiResponse<Page<ViewingHistoryResponse>> getLocalViewHistory(
      @RequestBody LocalHistoryRequest localHistoryRequest,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size) {
    Page<ViewingHistoryResponse> result = viewingHistoryService.getLocalViewHistory(
        localHistoryRequest, page, size);
    return ApiResponse.<Page<ViewingHistoryResponse>>builder()
        .message(MSG_HISTORY_FETCHED)
        .result(result)
        .build();
  }

  /**
   * Xoá lịch sử xem của một tập phim.
   *
   * @param episodeId mã định danh của tập phim cần xoá lịch sử
   * @return ApiResponse thông báo quá trình xoá lịch sử thành công
   */
  @PostMapping(ENDPOINT_DELETE_HISTORY)
  ApiResponse<Void> deleteViewHistory(@RequestParam String episodeId) {
    viewingHistoryService.deleteViewHistory(episodeId);
    return ApiResponse.<Void>builder()
        .message(MSG_HISTORY_DELETED)
        .build();
  }

  /**
   * Đồng bộ lịch sử xem từ localStorage sang DB.
   *
   * @param localHistoryRequest yêu cầu chứa thông tin lịch sử xem cục bộ
   * @return ApiResponse thông báo quá trình đồng bộ lịch sử thành công
   */
  @PostMapping(ENDPOINT_SYNC_HISTORY)
  ApiResponse<Void> syncLocalHistory(@RequestBody LocalHistoryRequest localHistoryRequest) {
    viewingHistoryService.syncLocalHistory(localHistoryRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_HISTORY_SYNCED)
        .build();
  }
}
