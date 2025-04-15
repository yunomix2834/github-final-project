package com.source.animeh.interface_package.service.file;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface UserFileServiceInterface {

  // Đổi ảnh avatar
  void uploadAvatar(MultipartFile file) throws IOException;

  // Đổi ảnh background
  void uploadBackground(MultipartFile file) throws IOException;

  // Lấy ảnh avatar
  Path getAvatarPath(String size) throws IOException;

  // Lấy ảnh background
  Path getBackgroundPath(String size) throws IOException;
}
