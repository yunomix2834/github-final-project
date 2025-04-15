package com.source.animeh.service.file;

import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.file.MediaStorageServiceInterface;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MediaStorageService implements MediaStorageServiceInterface {

  private static final String MEDIA_ROOT = "F:/fileHoc/animeh/image_library";

  // Lưu poster/banner cho Series
  // type = "poster" hoặc "banner"
  @Override
  public String storeSeriesMedia(
      MultipartFile file,
      String seriesId,
      String type)
      throws IOException {

    Path basePath = Paths.get(
        MEDIA_ROOT,
        "series",
        seriesId,
        type
    );

    Files.createDirectories(basePath);

    // Lấy extension
    String originalFilename = file.getOriginalFilename();
    List<String> ALLOWED_EXT = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");

    String extension = getFileExtension(originalFilename); // => "png", "jpg", ...
    if (!ALLOWED_EXT.contains(extension)) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
    }

    // Tạo folder con mang tên extension => .../type/<extension>
    Path extensionFolder = basePath.resolve(extension);
    // Xoá sạch folder extension cũ (nếu muốn), rồi tạo lại
    if (Files.exists(extensionFolder)) {
      deleteFolderRecursively(extensionFolder);
    }
    Files.createDirectories(extensionFolder);

    // Tạo file gốc
    Path pathOriginal = extensionFolder.resolve("original." + extension);
    // Tạo file tiny, small
    Path pathTiny = extensionFolder.resolve("tiny." + extension);
    Path pathSmall = extensionFolder.resolve("small." + extension);

    file.transferTo(pathOriginal.toFile());

    // Resize tiny (100x100 ví dụ)
    Thumbnails.of(pathOriginal.toFile())
        .size(100, 100)
        .toFile(pathTiny.toFile());

    // Resize small (300x300 ví dụ)
    Thumbnails.of(pathOriginal.toFile())
        .size(300, 300)
        .toFile(pathSmall.toFile());

    return "/media/series/" + seriesId + "/" + type + "/" + extension + "/";
  }

  // Lưu poster/banner cho Anime
  // type = "poster" hoặc "banner"
  @Override
  public String storeAnimeMedia(
      MultipartFile file,
      String animeId,
      String type)
      throws IOException {

    Path basePath = Paths.get(
        MEDIA_ROOT,
        "anime",
        animeId,
        type
    );

    Files.createDirectories(basePath);

    String originalFilename = file.getOriginalFilename();

    List<String> ALLOWED_EXT = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");
    String extension = getFileExtension(originalFilename); // => "png", "jpg", ...
    if (!ALLOWED_EXT.contains(extension)) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
    }

    // Tạo folder con mang tên extension => .../type/<extension>
    Path extensionFolder = basePath.resolve(extension);
    // Xoá sạch folder extension cũ (nếu muốn), rồi tạo lại
    if (Files.exists(extensionFolder)) {
      deleteFolderRecursively(extensionFolder);
    }
    Files.createDirectories(extensionFolder);

    Path pathOriginal = extensionFolder.resolve("original." + extension);
    Path pathTiny = extensionFolder.resolve("tiny." + extension);
    Path pathSmall = extensionFolder.resolve("small." + extension);

    file.transferTo(pathOriginal.toFile());

    // Resize tiny
    Thumbnails.of(pathOriginal.toFile())
        .size(100, 100)
        .toFile(pathTiny.toFile());

    // Resize small
    Thumbnails.of(pathOriginal.toFile())
        .size(300, 300)
        .toFile(pathSmall.toFile());

    return "/media/anime/" + animeId + "/" + type + "/" + extension + "/";
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
   * Xóa file tiny.*, small.*, original.* trong một folder cụ thể
   *
   * @param folderPath đường dẫn đến thư mục (VD: .../series/<seriesId>/poster)
   */
  void removeOldImagesFiles(
      String folderPath)
      throws IOException {

    Path folder = Paths.get(folderPath);
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
