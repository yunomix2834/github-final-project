package com.source.animeh.service.file;

import com.source.animeh.constant.file.PredefinedFile;
import com.source.animeh.entity.account.User;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.file.UserFileServiceInterface;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.utils.SecurityUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFileService implements UserFileServiceInterface {

  UserRepository userRepository;
  ImageStorageService imageStorageService;

  // Đổi ảnh avatar
  @Override
  public void uploadAvatar(
      MultipartFile file)
      throws IOException {

    User user = SecurityUtils.getCurrentUser(userRepository);
    if (user == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    // Resize avatar
    String avatarFolder = imageStorageService.storeUserImage(
        file,
        user.getId(),
        "avatar",
        PredefinedFile.AVATAR_TINY, PredefinedFile.AVATAR_TINY,
        PredefinedFile.AVATAR_SMALL, PredefinedFile.AVATAR_SMALL
    );

    // Lưu path original vào user
    user.setAvatarUrl(avatarFolder);

    userRepository.save(user);
  }

  // Đổi ảnh background
  @Override
  public void uploadBackground(
      MultipartFile file)
      throws IOException {

    User user = SecurityUtils.getCurrentUser(userRepository);
    if (user == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    String bgFolder = imageStorageService.storeUserImage(
        file,
        user.getId(),
        "background",
        PredefinedFile.BG_TINY_WIDTH, PredefinedFile.BG_TINY_HEIGHT,   // tiny
        PredefinedFile.BG_SMALL_WIDTH, PredefinedFile.BG_SMALL_HEIGHT    // small
    );

    user.setBackgroundUrl(bgFolder);

    userRepository.save(user);
  }

  // Lấy đường dẫn ảnh avatar
  @Override
  public Path getAvatarPath(String size) {
    User user = SecurityUtils.getCurrentUser(userRepository);
    if (user == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    return imageStorageService.getUserImagePath(
        user.getId(),
        "avatar",
        size);
  }

  // Lấy đường dẫn ảnh background
  @Override
  public Path getBackgroundPath(String size) {
    User user = SecurityUtils.getCurrentUser(userRepository);
    if (user == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    return imageStorageService.getUserImagePath(
        user.getId(),
        "background",
        size);
  }

  void removeOldImagesFiles(
      String userId,
      String type)
      throws IOException {

    // folder = /uploads/userId/type
    Path folder = imageStorageService
        .getBasePath()
        .resolve(userId)
        .resolve(type);
    if (!Files.exists(folder)) {
      return;
    }

    // Xoá file tiny.*, small.*, original.*
    String[] prefixes = {"tiny.", "small.", "original."};
    for (String prefix : prefixes) {
      Files.list(folder)
          .filter(p -> p.getFileName().toString().startsWith(prefix))
          .forEach(p -> {
            try {
              Files.deleteIfExists(p);
              log.info("Deleted old file: {}", p);
            } catch (IOException e) {
              log.warn("Cannot delete file: {}", p, e);
              throw new AppException(ErrorCode.FAILED_DELETE_FILE);
            }
          });
    }
  }

}
