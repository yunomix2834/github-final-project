package com.source.animeh.service.file;

import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.file.ImageStorageServiceInterface;
import com.source.animeh.repository.account.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Builder
public class ImageStorageService implements ImageStorageServiceInterface {

  // Đường dẫn gốc upload, inject từ application.properties
  // @NonFinal
  // @Value("${app.file.upload-dir}")
  private final String uploadDir = "D:/fileCode/animeh/image_test";
  UserRepository userRepository;

  /**
   * Lưu file upload của user, tạo 3 kích thước: tiny(50x50), small(200x200), original.
   *
   * @param file multipart file do user upload
   * @param type "avatar" hoặc "background"
   * @return Mảng string [pathTiny, pathSmall, pathOriginal]
   */
  @Override
  public String storeUserImage(
      MultipartFile file,
      String userId,
      String type,
      int tinyWidth, int tinyHeight,
      int smallWidth, int smallHeight
  ) throws IOException {

    // Tạo folder: /uploadDir/userId/type
    Path basePath = getBasePath().resolve(userId).resolve(type);
    Files.createDirectories(basePath);

    // Lấy extension => "png, jpg,..."
    // Tạo tên file
    List<String> ALLOWED_EXT = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");
    String extension = getFileExtension(file.getOriginalFilename());
    if (!ALLOWED_EXT.contains(extension)) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
    }

    Path extensionFolder = basePath.resolve(extension);
    if (Files.exists(extensionFolder)) {
      deleteFolderRecursively(extensionFolder);
    }
    Files.createDirectories(extensionFolder);

    // Tạo đường dẫn file gốc
    Path pathOriginal = extensionFolder.resolve("original." + extension);
    Path pathTiny = extensionFolder.resolve("tiny." + extension);
    Path pathSmall = extensionFolder.resolve("small." + extension);

    file.transferTo(pathOriginal.toFile());

    // Resize tiny
    Thumbnails.of(pathOriginal.toFile())
        .size(tinyWidth, tinyHeight)
        .toFile(pathTiny.toFile());

    // Resize small 200x200
    Thumbnails.of(pathOriginal.toFile())
        .size(smallWidth, smallHeight)
        .toFile(pathSmall.toFile());

    return "/profile-image/" + userId + "/" + type + "/" + extension + "/";
  }

  /**
   * Trả về file để hiển thị.
   *
   * @param type "avatar" or "background"
   * @param size "tiny", "small", "original"
   * @return Path file
   */
  @Override
  public Path getUserImagePath(
      String userId,
      String type,
      String size) {

    Path folder = getBasePath()
        .resolve(userId)
        .resolve(type);

    if (!Files.exists(folder)) {
      return null;
    }

    try {
      return Files.list(folder)
          .filter(p -> {
            String name = p.getFileName().toString();
            return name.startsWith(size + ".");
          })
          .findFirst()
          .orElse(null);
    } catch (IOException e) {
      throw new AppException(ErrorCode.FAILED_READ_FOLDER);
    }
  }

  Path getBasePath() {
    return Paths.get(uploadDir).toAbsolutePath().normalize();
  }

  String getFileExtension(String filename) {
    if (filename == null) {
      throw new AppException(ErrorCode.INVALID_FILE_NAME);
    }
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex < 0) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
    }
    return filename.substring(dotIndex + 1).toLowerCase();
  }

  /**
   * Hàm tiện ích: Xoá toàn bộ folder & file bên trong.
   */
  void deleteFolderRecursively(Path folder) throws IOException {
    if (!Files.exists(folder)) {
      return;
    }
    Files.walk(folder)
        .sorted((p1, p2) -> p2.compareTo(p1)) // xoá từ dưới lên
        .forEach(p -> {
          try {
            Files.deleteIfExists(p);
          } catch (IOException e) {
            throw new RuntimeException("Cannot delete: " + p, e);
          }
        });
  }
}