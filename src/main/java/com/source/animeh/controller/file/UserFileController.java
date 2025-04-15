package com.source.animeh.controller.file;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.service.file.ImageStorageService;
import com.source.animeh.service.file.UserFileService;
import com.source.animeh.utils.MediaUtils;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller xử lý các thao tác liên quan đến file của người dùng.
 * <p>
 * Các endpoint, thông báo và default value được định nghĩa dưới dạng hằng số để dễ dàng quản lý và
 * thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(UserFileController.BASE_ENDPOINT)
public class UserFileController {

  // Base endpoint
  public static final String BASE_ENDPOINT = "/user";
  // Endpoint upload
  public static final String ENDPOINT_UPLOAD_AVATAR = "/upload/avatar";
  public static final String ENDPOINT_UPLOAD_BACKGROUND = "/upload/background";
  // Endpoint get file
  public static final String ENDPOINT_GET_AVATAR = "/avatar";
  public static final String ENDPOINT_GET_BACKGROUND = "/background";
  // Các thông báo phản hồi
  public static final String MSG_UPLOAD_AVATAR_SUCCESS = "Uploaded avatar successfully!";
  public static final String MSG_UPLOAD_BACKGROUND_SUCCESS = "Uploaded background successfully!";
  // Default value
  public static final String DEFAULT_SIZE = "original";
  ImageStorageService imageStorageService;
  UserFileService userFileService;

  /**
   * Upload avatar của người dùng.
   *
   * @param file file ảnh avatar được upload
   * @return ApiResponse thông báo quá trình upload thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý file
   */
  @PostMapping(value = ENDPOINT_UPLOAD_AVATAR, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<Void> uploadAvatar(@RequestParam("file") MultipartFile file)
      throws IOException {
    userFileService.uploadAvatar(file);
    return ApiResponse.<Void>builder()
        .message(MSG_UPLOAD_AVATAR_SUCCESS)
        .build();
  }

  /**
   * Upload background của người dùng.
   *
   * @param file file ảnh background được upload
   * @return ApiResponse thông báo quá trình upload thành công
   * @throws IOException nếu có lỗi trong quá trình xử lý file
   */
  @PostMapping(value = ENDPOINT_UPLOAD_BACKGROUND, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<Void> uploadBackground(@RequestParam("file") MultipartFile file)
      throws IOException {
    userFileService.uploadBackground(file);
    return ApiResponse.<Void>builder()
        .message(MSG_UPLOAD_BACKGROUND_SUCCESS)
        .build();
  }

  /**
   * Lấy avatar của người dùng.
   *
   * @param size kích thước avatar (mặc định là "original")
   * @return ResponseEntity chứa Resource của file avatar
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_GET_AVATAR)
  ResponseEntity<Resource> getAvatar(
      @RequestParam(name = "size", defaultValue = DEFAULT_SIZE) String size)
      throws IOException {
    Path path = userFileService.getAvatarPath(size);
    return getResourceResponseEntity(path);
  }

  /**
   * Lấy background của người dùng.
   *
   * @param size kích thước background (mặc định là "original")
   * @return ResponseEntity chứa Resource của file background
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_GET_BACKGROUND)
  ResponseEntity<Resource> getBackground(
      @RequestParam(defaultValue = DEFAULT_SIZE) String size)
      throws IOException {
    Path path = userFileService.getBackgroundPath(size);
    return getResourceResponseEntity(path);
  }

  /**
   * Xây dựng ResponseEntity chứa Resource dựa trên đường dẫn file.
   *
   * @param path đường dẫn đến file
   * @return ResponseEntity chứa Resource của file nếu tồn tại
   * @throws MalformedURLException nếu URL không hợp lệ
   */
  @NotNull
  ResponseEntity<Resource> getResourceResponseEntity(Path path)
      throws MalformedURLException {
    if (path == null || !Files.exists(path)) {
      throw new AppException(ErrorCode.FILE_NOT_FOUND);
    }
    MediaType contentType = MediaUtils.detectMediaType(path);
    Resource resource = new UrlResource(path.toUri());
    return ResponseEntity.ok()
        .contentType(contentType)
        .body(resource);
  }
}
