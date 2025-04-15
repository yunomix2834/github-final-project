package com.source.animeh.service.account;

import com.source.animeh.constant.PredefinedStatus;
import com.source.animeh.dto.request.account.UserUpdatePasswordRequest;
import com.source.animeh.dto.request.account.UserUpdateRequest;
import com.source.animeh.dto.response.account.ModeratorProfileResponse;
import com.source.animeh.dto.response.account.ProfileResponse;
import com.source.animeh.dto.response.account.UserResponse;
import com.source.animeh.entity.account.ModeratorRequest;
import com.source.animeh.entity.account.User;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.account.UserServiceInterface;
import com.source.animeh.mapper.account.UserMapper;
import com.source.animeh.repository.account.ModeratorRequestRepository;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.utils.SecurityUtils;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserServiceInterface {

  UserRepository userRepository;
  UserMapper userMapper;
  ModeratorRequestRepository moderatorRequestRepository;
  PasswordEncoder passwordEncoder;

  // Tìm User thông qua Username
  public User findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  public User findByUsernameNotThrowError(String username) {
    return userRepository
        .findByUsername(username)
        .orElse(null);
  }

  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  public User findByEmailNotThrowError(String username) {
    return userRepository
        .findByEmail(username)
        .orElse(null);
  }

  // Tìm User thông qua id
  User findById(String id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  // List tất cả các User
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public Page<UserResponse> getAllUsers(int page, int size) {

    // Lấy tất cả user
    Pageable pageable = PageRequest.of(page, size);
    Page<User> pageData = userRepository.findAll(pageable);

    // Duyệt từng user -> map -> check request PENDING
    return pageData.map(user -> {
      UserResponse userResponse = userMapper.toUserResponse(user);

      // Lấy request PENDING mới nhất của user
      ModeratorRequest pendingRequest = moderatorRequestRepository
          .findFirstByUserAndStatusOrderByCreatedAtDesc(
              user,
              PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus()
          );

      // Nếu có request -> set status vào response dto
      if (pendingRequest != null) {
        userResponse.setModeratorRequestStatus(
            pendingRequest.getStatus() // "pending"
        );
      }

      return userResponse;
    });
  }

  // Tìm User qua id
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse getUserById(String id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    UserResponse userResponse = userMapper.toUserResponse(user);

    // Lấy request PENDING mới nhất của user
    ModeratorRequest pendingRequest = moderatorRequestRepository
        .findFirstByUserAndStatusOrderByCreatedAtDesc(
            user,
            PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus()
        );

    // Nếu có request -> set status vào response dto
    if (pendingRequest != null) {
      userResponse.setModeratorRequestStatus(
          pendingRequest.getStatus() // "pending"
      );
    }

    return userResponse;
  }

  // Get thông tin cá nhân của bản thân
  @Override
  @PostAuthorize("returnObject.username == authentication.name")
  public ProfileResponse getProfile() {

    User user = SecurityUtils.getCurrentUser(userRepository);

    return userMapper.toProfileResponse(user);
  }

  @Override
  @PostAuthorize("returnObject.username == authentication.name")
  public ModeratorProfileResponse getModeratorProfile() {
    User user = SecurityUtils.getCurrentUser(userRepository);
    return userMapper.toModeratorProfileResponse(user);
  }

  // Khoá tài khoản User
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public void deactivateUserById(String id) {
    User user = findById(id);
    user.setIsActive(false);
    userRepository.save(user);
  }

  // TODO KHI NÀO CẬP NHẬT SỬA LẠI THÀNH FILEUPLOAD
  // User tự cập nhật thông tin của bản thân
  @Override
  public void updateUser(
      UserUpdateRequest userUpdateRequest) {

    User existingUser = SecurityUtils.getCurrentUser(userRepository);

    userMapper.updateUser(userUpdateRequest, existingUser);
    existingUser.setUpdatedAt(LocalDateTime.now());

    userRepository.save(existingUser);
  }

  // User tự cập nhật mật khẩu của bản thân
  @Override
  public void updateUserPassword(

      UserUpdatePasswordRequest userUpdatePasswordRequest) {

    User existingUser = SecurityUtils.getCurrentUser(userRepository);

    if (!passwordEncoder.matches(
        userUpdatePasswordRequest.getOldPassword(), existingUser.getPassword())) {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    existingUser.setPassword(
        passwordEncoder.encode(userUpdatePasswordRequest.getNewPassword())
    );
    existingUser.setUpdatedAt(LocalDateTime.now());

    userRepository.save(existingUser);
  }
}