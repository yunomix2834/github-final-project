package com.source.animeh.interface_package.service.account;

import com.source.animeh.dto.request.account.UserUpdatePasswordRequest;
import com.source.animeh.dto.request.account.UserUpdateRequest;
import com.source.animeh.dto.response.account.ModeratorProfileResponse;
import com.source.animeh.dto.response.account.ProfileResponse;
import com.source.animeh.dto.response.account.UserResponse;
import org.springframework.data.domain.Page;

public interface UserServiceInterface {

  // List tất cả các User
  Page<UserResponse> getAllUsers(int page, int size);

  // Tìm User qua id
  UserResponse getUserById(String id);

  // Get thông tin cá nhân của bản thân
  ProfileResponse getProfile();

  // Get thông tin cá nhân để đăng ký Moderator
  ModeratorProfileResponse getModeratorProfile();

  // Khoá tài khoản User
  void deactivateUserById(String id);

  // TODO KHI NÀO CẬP NHẬT SỬA LẠI THÀNH FILEUPLOAD
  // User tự cập nhật thông tin của bản thân
  void updateUser(
      UserUpdateRequest userUpdateRequest);

  // User tự cập nhật mật khẩu của bản thân
  void updateUserPassword(
      UserUpdatePasswordRequest userUpdatePasswordRequest);

}
