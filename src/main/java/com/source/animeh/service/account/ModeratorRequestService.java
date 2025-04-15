package com.source.animeh.service.account;

import com.source.animeh.constant.PredefinedStatus;
import com.source.animeh.constant.account.PredefinedRole;
import com.source.animeh.dto.request.account.request_moderator.ApprovalModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.GuestModeratorRequest;
import com.source.animeh.dto.request.account.request_moderator.UserModeratorRequest;
import com.source.animeh.dto.response.account.ModeratorRequest_Response;
import com.source.animeh.entity.account.ModeratorRequest;
import com.source.animeh.entity.account.Role;
import com.source.animeh.entity.account.User;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.account.ModeratorRequestServiceInterface;
import com.source.animeh.mapper.account.ModeratorRequestMapper;
import com.source.animeh.repository.account.ModeratorRequestRepository;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModeratorRequestService implements ModeratorRequestServiceInterface {

  UserService userService;
  RoleService roleService;
  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  ModeratorRequestMapper moderatorRequestMapper;
  ModeratorRequestRepository moderatorRequestRepository;

  @PreAuthorize("hasRole('ADMIN')")
  public Page<ModeratorRequest_Response> getAllModeratorRequests(int page, int size) {
    Page<ModeratorRequest> pageData = moderatorRequestRepository.findAll(
        PageRequest.of(page, size));

    return pageData.map(moderatorRequestMapper::toModeratorRequest_Response);
  }

  // Lấy Moderator Request theo Id
  @PreAuthorize("hasRole('ADMIN')")
  public ModeratorRequest_Response getModeratorRequestById(String requestId) {
    ModeratorRequest request = moderatorRequestRepository.findById(requestId)
        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

    return moderatorRequestMapper.toModeratorRequest_Response(request);
  }

  // Lấy Moderator Request theo User Id
  @PreAuthorize("hasRole('ADMIN')")
  public Page<ModeratorRequest_Response> getModeratorRequestsByUserId(
      String userId,
      int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ModeratorRequest> data = moderatorRequestRepository.findByUserId(userId, pageable);

    return data.map(moderatorRequestMapper::toModeratorRequest_Response);
  }

  // Đăng ký Moderator cho Guest
  @Override
  @PreAuthorize("!hasRole('MODERATOR')")
  public ModeratorRequest_Response requestModeratorForGuest(
      GuestModeratorRequest guestModeratorRequest) {

    // TODO Sửa lại hàm findByUsername, findByEmail
    // Kiểm tra username / email đã tồn tại chưa
    if (userService.findByUsernameNotThrowError(
        guestModeratorRequest.getUsername()) != null) {
      throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    if (userService.findByEmailNotThrowError(
        guestModeratorRequest.getEmail()) != null) {
      throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    // Lấy role User
    Role userRole = roleService.findRoleByName(PredefinedRole.USER);

    // Tạo User mới
    User newUser = new User();
    newUser.setId(UUID.randomUUID().toString());
    newUser.setUsername(guestModeratorRequest.getUsername());
    newUser.setPassword(
        passwordEncoder.encode(guestModeratorRequest.getPassword()));
    newUser.setEmail(guestModeratorRequest.getEmail());
    newUser.setRole(userRole);
    newUser.setFullName(guestModeratorRequest.getFullName());
    newUser.setCccd(guestModeratorRequest.getCccd());
    newUser.setPhoneNumber(guestModeratorRequest.getPhoneNumber());
    newUser.setIsActive(true);

    // Lưu User
    User savedUser = userRepository.save(newUser);

    // Tạo request
    return getModeratorRequestResponse(savedUser);
  }

  // Đăng ký Moderator cho User
  @Override
  @PreAuthorize("!hasRole('MODERATOR')")
  public ModeratorRequest_Response requestModeratorForUser(
      UserModeratorRequest userModeratorRequest) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Cập nhật lại các thông tin
    user.setFullName(userModeratorRequest.getFullName());
    user.setCccd(userModeratorRequest.getCccd());
    user.setPhoneNumber(userModeratorRequest.getPhoneNumber());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    // Tạo request
    return getModeratorRequestResponse(user);
  }

  // Lấy Moderator request
  @PreAuthorize("!hasRole('MODERATOR')")
  ModeratorRequest_Response getModeratorRequestResponse(User user) {
    ModeratorRequest request = new ModeratorRequest();
    request.setId(UUID.randomUUID().toString());
    request.setUser(user);
    request.setRequestDate(LocalDateTime.now());
    request.setStatus(PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus());
    request.setCreatedAt(LocalDateTime.now());

    return moderatorRequestMapper.toModeratorRequest_Response(
        moderatorRequestRepository.save(request));
  }


  // Duyệt Moderator
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ModeratorRequest_Response approveModeratorRequest(
      String requestId) {

    User admin = SecurityUtils.getCurrentUser(userRepository);

    ModeratorRequest request = findRequestById(requestId);

    if (!request.getStatus()
        .equals(PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus())) {
      throw new AppException(ErrorCode.INVALID_REQUEST_STATUS);
    }

    // Lấy Role Moderator
    Role moderatorRole = roleService.findRoleByName(PredefinedRole.MOD);

    // Cập nhật trạng thái user
    User user = request.getUser();
    user.setRole(moderatorRole);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    // Cập nhật request
    request.setStatus(PredefinedStatus.MODERATOR_REQUEST_APPROVED.getStatus());
    request.setAdmin(admin);
    request.setProcessedAt(LocalDateTime.now());
    request.setUpdatedAt(LocalDateTime.now());

    return moderatorRequestMapper.toModeratorRequest_Response(
        moderatorRequestRepository.save(request));
  }

  // Từ chối Moderator
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ModeratorRequest_Response rejectModeratorRequest(
      ApprovalModeratorRequest approvalModeratorRequest,
      String requestId) {

    User admin = SecurityUtils.getCurrentUser(userRepository);
    ModeratorRequest request = findRequestById(requestId);

    if (!request.getStatus()
        .equals(PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus())) {
      throw new AppException(ErrorCode.INVALID_REQUEST_STATUS);
    }

    // Cập nhật request
    request.setAdmin(admin);
    request.setStatus(PredefinedStatus.MODERATOR_REQUEST_REJECTED.getStatus());
    request.setCommentRejected(approvalModeratorRequest.getCommentRejected());
    request.setProcessedAt(LocalDateTime.now());
    request.setUpdatedAt(LocalDateTime.now());

    request = moderatorRequestRepository.save(request);
    return moderatorRequestMapper.toModeratorRequest_Response(request);
  }

  ModeratorRequest findRequestById(String requestId) {
    return moderatorRequestRepository
        .findById(requestId)
        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<ModeratorRequest> getNewestPendingRequests(
      int limit) {
    return moderatorRequestRepository
        .findByStatusOrderByCreatedAtDesc(
            PredefinedStatus.MODERATOR_REQUEST_PENDING.getStatus(), PageRequest.of(0, limit));
  }

}
