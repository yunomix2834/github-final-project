package com.source.animeh.interface_package.service.file;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageServiceInterface {

  /**
   * Lưu file upload của user, tạo 3 kích thước: tiny(50x50), small(200x200), original.
   *
   * @param file multipart file do user upload
   * @param type "avatar" hoặc "background"
   * @return Mảng string [pathTiny, pathSmall, pathOriginal]
   */
  String storeUserImage(
      MultipartFile file,
      String userId,
      String type,
      int tinyWidth, int tinyHeight,
      int smallWidth, int smallHeight
  ) throws IOException;

  /**
   * Trả về file để hiển thị.
   *
   * @param type "avatar" or "background"
   * @param size "tiny", "small", "original"
   * @return Path file
   */
  Path getUserImagePath(
      String userId,
      String type,
      String size);
}
