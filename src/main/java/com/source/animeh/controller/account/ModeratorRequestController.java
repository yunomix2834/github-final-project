package com.source.animeh.controller.account;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.account.request_moderator.ApprovalModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.GuestModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.UserModeratorRequest;
import com.source.animeh.dto.response.account.ModeratorRequest_Response;
import com.source.animeh.service.account.ModeratorRequestService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các yêu cầu đăng ký Moderator.
 * <p>
 * Các endpoint và thông báo được định nghĩa dưới dạng hằng số để dễ dàng quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(ModeratorRequestController.ENDPOINT_BASE)
public class ModeratorRequestController {

  // Base endpoint cho controller
  public static final String ENDPOINT_BASE = "/moderator-register";
  // Các endpoint con
  public static final String ENDPOINT_GUEST = "/guest";
  public static final String ENDPOINT_USER = "/user";
  public static final String ENDPOINT_ADMIN_APPROVE = "/admin/approve/{requestId}";
  public static final String ENDPOINT_ADMIN_REJECT = "/admin/rejected/{requestId}";
  public static final String ENDPOINT_ADMIN_REQUESTS = "/admin/requests";
  public static final String ENDPOINT_ADMIN_REQUEST_BY_ID = "/admin/request/{requestId}";
  public static final String ENDPOINT_ADMIN_REQUEST_BY_USER = "/admin/request/by-user/{userId}";
  // Các thông báo phản hồi
  public static final String MSG_REGISTER_MODERATOR_SUCCESS = "Register Moderator Successfully!";
  public static final String MSG_APPROVE_MODERATOR_SUCCESS = "Approve Moderator Successfully!";
  public static final String MSG_REJECT_MODERATOR_SUCCESS = "Reject Moderator Succesfully!";
  public static final String MSG_GET_ALL_MODERATOR_REQUESTS_SUCCESS = "Get all moderator requests successfully!";
  public static final String MSG_GET_MODERATOR_REQUEST_BY_ID_SUCCESS = "Get moderator request by id successfully!";
  public static final String MSG_GET_MODERATOR_REQUESTS_BY_USER_SUCCESS = "Get Moderator Requests by userId successfully!";
  ModeratorRequestService moderatorRequestService;

  /**
   * Yêu cầu đăng ký Moderator cho Guest.
   *
   * @param guestModeratorRequest thông tin đăng ký Moderator của Guest
   * @return ApiResponse chứa thông tin yêu cầu đăng ký Moderator
   */
  @PostMapping(ENDPOINT_GUEST)
  ApiResponse<ModeratorRequest_Response> requestModeratorForGuest(
      @RequestBody GuestModeratorRequest guestModeratorRequest) {
    ModeratorRequest_Response response = moderatorRequestService.requestModeratorForGuest(
        guestModeratorRequest);
    return ApiResponse.<ModeratorRequest_Response>builder()
        .message(MSG_REGISTER_MODERATOR_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Yêu cầu đăng ký Moderator cho User.
   *
   * @param userModeratorRequest thông tin đăng ký Moderator của User
   * @return ApiResponse chứa thông tin yêu cầu đăng ký Moderator
   */
  @PostMapping(ENDPOINT_USER)
  ApiResponse<ModeratorRequest_Response> requestModeratorForUser(
      @RequestBody UserModeratorRequest userModeratorRequest) {
    ModeratorRequest_Response response = moderatorRequestService.requestModeratorForUser(
        userModeratorRequest);
    return ApiResponse.<ModeratorRequest_Response>builder()
        .message(MSG_REGISTER_MODERATOR_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Duyệt yêu cầu đăng ký Moderator.
   *
   * @param requestId mã yêu cầu
   * @return ApiResponse chứa thông tin yêu cầu đã được duyệt
   */
  @PostMapping(ENDPOINT_ADMIN_APPROVE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<ModeratorRequest_Response> approveModeratorRequest(
      @PathVariable("requestId") String requestId) {
    ModeratorRequest_Response response = moderatorRequestService.approveModeratorRequest(requestId);
    return ApiResponse.<ModeratorRequest_Response>builder()
        .message(MSG_APPROVE_MODERATOR_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Từ chối yêu cầu đăng ký Moderator.
   *
   * @param requestId                mã yêu cầu
   * @param approvalModeratorRequest thông tin từ chối yêu cầu
   * @return ApiResponse chứa thông tin yêu cầu đã bị từ chối
   */
  @PostMapping(ENDPOINT_ADMIN_REJECT)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<ModeratorRequest_Response> rejectModeratorRequest(
      @PathVariable("requestId") String requestId,
      @RequestBody ApprovalModeratorRequest approvalModeratorRequest) {
    ModeratorRequest_Response response = moderatorRequestService.rejectModeratorRequest(
        approvalModeratorRequest, requestId);
    return ApiResponse.<ModeratorRequest_Response>builder()
        .message(MSG_REJECT_MODERATOR_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Lấy danh sách tất cả các yêu cầu đăng ký Moderator.
   *
   * @param page số trang (mặc định là 0)
   * @param size kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách các yêu cầu đăng ký Moderator dạng phân trang
   */
  @GetMapping(ENDPOINT_ADMIN_REQUESTS)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Page<ModeratorRequest_Response>> getAllModeratorRequests(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<ModeratorRequest_Response> result = moderatorRequestService.getAllModeratorRequests(page,
        size);
    return ApiResponse.<Page<ModeratorRequest_Response>>builder()
        .message(MSG_GET_ALL_MODERATOR_REQUESTS_SUCCESS)
        .result(result)
        .build();
  }

  /**
   * Lấy yêu cầu đăng ký Moderator theo ID.
   *
   * @param requestId mã yêu cầu
   * @return ApiResponse chứa thông tin yêu cầu đăng ký Moderator
   */
  @GetMapping(ENDPOINT_ADMIN_REQUEST_BY_ID)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<ModeratorRequest_Response> getModeratorRequestById(
      @PathVariable String requestId) {
    ModeratorRequest_Response response = moderatorRequestService.getModeratorRequestById(requestId);
    return ApiResponse.<ModeratorRequest_Response>builder()
        .message(MSG_GET_MODERATOR_REQUEST_BY_ID_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Lấy danh sách yêu cầu đăng ký Moderator theo user ID.
   *
   * @param userId mã người dùng
   * @param page   số trang (mặc định là 0)
   * @param size   kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách các yêu cầu đăng ký Moderator của user dạng phân trang
   */
  @GetMapping(ENDPOINT_ADMIN_REQUEST_BY_USER)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Page<ModeratorRequest_Response>> getModeratorRequestsByUserId(
      @PathVariable String userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<ModeratorRequest_Response> result = moderatorRequestService.getModeratorRequestsByUserId(
        userId, page, size);
    return ApiResponse.<Page<ModeratorRequest_Response>>builder()
        .message(MSG_GET_MODERATOR_REQUESTS_BY_USER_SUCCESS)
        .result(result)
        .build();
  }
}
