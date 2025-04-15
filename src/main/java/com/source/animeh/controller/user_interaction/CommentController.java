package com.source.animeh.controller.user_interaction;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.user_interaction.CommentCreateRequest;
import com.source.animeh.dto.response.user_interaction.CommentResponse;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.service.user_interaction.CommentService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác liên quan đến Comment của Anime.
 * <p>
 * Các thao tác bao gồm: tạo comment gốc, trả lời comment, lấy danh sách comment theo các tiêu chí
 * khác nhau, cũng như ẩn/mở comment dành cho ADMIN. Các endpoint, thông báo và default value được
 * định nghĩa dưới dạng hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class CommentController {

  // --- Endpoint Paths ---
  public static final String ENDPOINT_CREATE_COMMENT = "/comments/create";
  public static final String ENDPOINT_REPLY_COMMENT = "/comments/reply";
  public static final String ENDPOINT_GET_ACTIVE_ROOT_COMMENTS = "/public/comments/active-root";
  public static final String ENDPOINT_GET_ALL_ROOT_COMMENTS = "/comments/all-root";
  public static final String ENDPOINT_GET_ALL_COMMENTS = "/comments/all";
  public static final String ENDPOINT_GET_ACTIVE_COMMENTS = "/public/comments/active";
  public static final String ENDPOINT_HIDE_COMMENT = "/comments/hide";
  public static final String ENDPOINT_ACTIVE_COMMENT = "/comments/active";
  // --- Thông báo phản hồi ---
  public static final String MSG_COMMENT_CREATED = "Comment created successfully!";
  public static final String MSG_REPLY_COMMENT = "Reply comment successfully!";
  public static final String MSG_GET_ACTIVE_ROOT_COMMENTS = "Get all active root comments successfully!";
  public static final String MSG_GET_ALL_ROOT_COMMENTS = "Get all root comments successfully!";
  public static final String MSG_GET_ALL_COMMENTS = "Get all comments success!";
  public static final String MSG_GET_ACTIVE_COMMENTS = "Get all active comments success!";
  public static final String MSG_COMMENT_DEACTIVATED = "Comment deactivated successfully!";
  public static final String MSG_COMMENT_ACTIVATED = "Comment activated successfully!";
  CommentService commentService;
  UserRepository userRepository;

  /**
   * Tạo comment gốc cho Anime.
   *
   * @param req yêu cầu tạo comment chứa thông tin animeId và nội dung comment
   * @return ApiResponse chứa thông tin comment vừa được tạo
   */
  @PostMapping(ENDPOINT_CREATE_COMMENT)
  ApiResponse<CommentResponse> createComment(@RequestBody CommentCreateRequest req) {
    CommentResponse result = commentService.createComment(req.getAnimeId(), req.getContent());
    return ApiResponse.<CommentResponse>builder()
        .message(MSG_COMMENT_CREATED)
        .result(result)
        .build();
  }

  /**
   * Tạo phản hồi (reply) cho một comment đã tồn tại.
   *
   * @param req yêu cầu tạo reply comment chứa thông tin animeId, parentId và nội dung comment
   * @return ApiResponse chứa thông tin comment reply vừa được tạo
   */
  @PostMapping(ENDPOINT_REPLY_COMMENT)
  ApiResponse<CommentResponse> replyComment(@RequestBody CommentCreateRequest req) {
    CommentResponse result = commentService.replyComment(req.getAnimeId(), req.getParentId(),
        req.getContent());
    return ApiResponse.<CommentResponse>builder()
        .message(MSG_REPLY_COMMENT)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách comment gốc đang hoạt động của một Anime.
   *
   * @param animeId mã định danh của Anime
   * @param page    số trang (mặc định là 0)
   * @param size    kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách comment gốc đang hoạt động dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_ACTIVE_ROOT_COMMENTS)
  ApiResponse<Page<CommentResponse>> getActiveRootComments(
      @RequestParam String animeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<CommentResponse> result = commentService.getActiveRootComments(animeId, page, size);
    return ApiResponse.<Page<CommentResponse>>builder()
        .message(MSG_GET_ACTIVE_ROOT_COMMENTS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách tất cả comment gốc của một Anime (dành cho ADMIN).
   *
   * @param animeId mã định danh của Anime
   * @param page    số trang (mặc định là 0)
   * @param size    kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách tất cả comment gốc dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_ALL_ROOT_COMMENTS)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Page<CommentResponse>> getAllRootComments(
      @RequestParam String animeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<CommentResponse> result = commentService.getAllRootComments(animeId, page, size);
    return ApiResponse.<Page<CommentResponse>>builder()
        .message(MSG_GET_ALL_ROOT_COMMENTS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách tất cả comment (bao gồm cả ẩn) của một Anime (dành cho ADMIN).
   *
   * @param animeId mã định danh của Anime
   * @param page    số trang (mặc định là 0)
   * @param size    kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách comment dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_ALL_COMMENTS)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Page<CommentResponse>> getAllComments(
      @RequestParam String animeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<CommentResponse> result = commentService.getAllComments(animeId, page, size);
    return ApiResponse.<Page<CommentResponse>>builder()
        .message(MSG_GET_ALL_COMMENTS)
        .result(result)
        .build();
  }

  /**
   * Lấy danh sách comment đang hoạt động của một Anime.
   *
   * @param animeId mã định danh của Anime
   * @param page    số trang (mặc định là 0)
   * @param size    kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách comment đang hoạt động dạng phân trang
   */
  @GetMapping(ENDPOINT_GET_ACTIVE_COMMENTS)
  ApiResponse<Page<CommentResponse>> getActiveComments(
      @RequestParam String animeId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<CommentResponse> result = commentService.getActiveComments(animeId, page, size);
    return ApiResponse.<Page<CommentResponse>>builder()
        .message(MSG_GET_ACTIVE_COMMENTS)
        .result(result)
        .build();
  }

  /**
   * Ẩn comment (deactivate comment) - chỉ ADMIN có thể thao tác.
   *
   * @param commentId mã định danh của comment cần ẩn
   * @return ApiResponse thông báo quá trình ẩn comment thành công
   */
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(ENDPOINT_HIDE_COMMENT)
  ApiResponse<Void> deactiveComment(@RequestParam String commentId) {
    commentService.deactiveComment(commentId);
    return ApiResponse.<Void>builder()
        .message(MSG_COMMENT_DEACTIVATED)
        .build();
  }

  /**
   * Mở lại comment đã bị ẩn (activate comment) - chỉ ADMIN có thể thao tác.
   *
   * @param commentId mã định danh của comment cần mở lại
   * @return ApiResponse thông báo quá trình mở lại comment thành công
   */
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(ENDPOINT_ACTIVE_COMMENT)
  ApiResponse<Void> activeComment(@RequestParam String commentId) {
    commentService.activeComment(commentId);
    return ApiResponse.<Void>builder()
        .message(MSG_COMMENT_ACTIVATED)
        .build();
  }
}
