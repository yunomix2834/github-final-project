package com.source.animeh.controller.account;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.account.UserUpdatePasswordRequest;
import com.source.animeh.dto.request.account.UserUpdateRequest;
import com.source.animeh.dto.response.account.ModeratorProfileResponse;
import com.source.animeh.dto.response.account.ProfileResponse;
import com.source.animeh.dto.response.account.UserResponse;
import com.source.animeh.service.account.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác liên quan đến User.
 * <p>
 * Các endpoint và thông báo phản hồi được định nghĩa dưới dạng hằng số để dễ dàng quản lý và thay
 * đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserController {

  // Base endpoint của admin và người dùng
  public static final String ADMIN_USERS_ENDPOINT = "/admin/users";
  public static final String ADMIN_USER_BY_ID_ENDPOINT = "/admin/user/{id}";
  public static final String ADMIN_DEACTIVATE_USER_ENDPOINT = "/admin/user/{id}";
  public static final String USER_PROFILE_ENDPOINT = "/user/profile";
  public static final String MODERATOR_PROFILE_ENDPOINT = "/moderator-register/profile";
  public static final String UPDATE_USER_ENDPOINT = "/user";
  public static final String UPDATE_USER_PASSWORD_ENDPOINT = "/user/password";
  // Các thông báo phản hồi
  public static final String MSG_GET_ALL_USERS_SUCCESS = "Get All Users Successfully!";
  public static final String MSG_GET_USER_SUCCESS = "Get User Successfully!";
  public static final String MSG_DEACTIVATE_USER_SUCCESS = "Deactivate User Successfully!";
  public static final String MSG_GET_PROFILE_SUCCESS = "Get User Profile Successfully!";
  public static final String MSG_GET_MODERATOR_PROFILE_SUCCESS = "Get User Profile Register Moderator Successfully!";
  public static final String MSG_UPDATE_USER_SUCCESS = "Update Successfully!";
  public static final String MSG_UPDATE_PASSWORD_SUCCESS = "Update Password Successfully!";
  UserService userService;

  /**
   * Lấy danh sách tất cả các User.
   *
   * @param page số trang (mặc định là 0)
   * @param size kích thước trang (mặc định là 10)
   * @return ApiResponse chứa danh sách User dạng phân trang
   */
  @GetMapping(ADMIN_USERS_ENDPOINT)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Page<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<UserResponse> users = userService.getAllUsers(page, size);
    return ApiResponse.<Page<UserResponse>>builder()
        .message(MSG_GET_ALL_USERS_SUCCESS)
        .result(users)
        .build();
  }

  /**
   * Tìm kiếm thông tin của User thông qua ID.
   *
   * @param id mã định danh của User
   * @return ApiResponse chứa thông tin User
   */
  @GetMapping(ADMIN_USER_BY_ID_ENDPOINT)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<UserResponse> getUserById(@PathVariable String id) {
    UserResponse user = userService.getUserById(id);
    return ApiResponse.<UserResponse>builder()
        .message(MSG_GET_USER_SUCCESS)
        .result(user)
        .build();
  }

  /**
   * Khoá tài khoản của User thông qua ID.
   *
   * @param id mã định danh của User cần khoá
   * @return ApiResponse thông báo quá trình khoá tài khoản thành công
   */
  @PatchMapping(ADMIN_DEACTIVATE_USER_ENDPOINT)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Void> deactivateUserById(@PathVariable String id) {
    userService.deactivateUserById(id);
    return ApiResponse.<Void>builder()
        .message(MSG_DEACTIVATE_USER_SUCCESS)
        .build();
  }

  /**
   * Lấy thông tin cá nhân của người dùng đang đăng nhập.
   *
   * @return ApiResponse chứa thông tin cá nhân của User
   */
  @GetMapping(USER_PROFILE_ENDPOINT)
  ApiResponse<ProfileResponse> getProfile() {
    ProfileResponse profile = userService.getProfile();
    return ApiResponse.<ProfileResponse>builder()
        .message(MSG_GET_PROFILE_SUCCESS)
        .result(profile)
        .build();
  }

  /**
   * Lấy thông tin cá nhân cần thiết để đăng ký làm Moderator.
   *
   * @return ApiResponse chứa thông tin cá nhân của User để đăng ký Moderator
   */
  @GetMapping(MODERATOR_PROFILE_ENDPOINT)
  ApiResponse<ModeratorProfileResponse> getModeratorProfile() {
    ModeratorProfileResponse profile = userService.getModeratorProfile();
    return ApiResponse.<ModeratorProfileResponse>builder()
        .message(MSG_GET_MODERATOR_PROFILE_SUCCESS)
        .result(profile)
        .build();
  }

  /**
   * Cập nhật thông tin cá nhân của người dùng.
   *
   * @param userUpdateRequest yêu cầu cập nhật thông tin của User
   * @return ApiResponse thông báo quá trình cập nhật thành công
   */
  @PatchMapping(UPDATE_USER_ENDPOINT)
  ApiResponse<Void> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
    userService.updateUser(userUpdateRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_UPDATE_USER_SUCCESS)
        .build();
  }

  /**
   * Cập nhật mật khẩu của người dùng.
   *
   * @param userUpdatePasswordRequest yêu cầu cập nhật mật khẩu của User
   * @return ApiResponse thông báo quá trình cập nhật mật khẩu thành công
   */
  @PatchMapping(UPDATE_USER_PASSWORD_ENDPOINT)
  ApiResponse<Void> updateUserPassword(
      @RequestBody @Valid UserUpdatePasswordRequest userUpdatePasswordRequest) {
    userService.updateUserPassword(userUpdatePasswordRequest);
    return ApiResponse.<Void>builder()
        .message(MSG_UPDATE_PASSWORD_SUCCESS)
        .build();
  }
}
