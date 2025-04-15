package com.source.animeh.controller.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý streaming video HLS.
 * <p>
 * Controller này cung cấp các endpoint để lấy file master playlist, variant playlist (dựa theo
 * folder được truyền qua query parameter) và các segment của HLS. Các endpoint, thông báo,
 * content-type và thư mục gốc được định nghĩa dưới dạng hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(HlsStreamController.BASE_ENDPOINT)
public class HlsStreamController {

  // --- Các hằng số cấu hình ---
  public static final String BASE_ENDPOINT = "/stream";
  public static final String HLS_ROOT = "F:\\fileHoc\\animeh\\video_test";

  // Endpoint
  public static final String ENDPOINT_MASTER = "/master";
  public static final String ENDPOINT_VARIANT = "/variant"; // folder được truyền dưới dạng query parameter
  public static final String ENDPOINT_SEGMENT = "/{folder}/{seg}";

  // Content-Type
  public static final String CONTENT_TYPE_HLS_PLAYLIST = "application/vnd.apple.mpegurl";
  public static final String CONTENT_TYPE_VIDEO_MP2T = "video/mp2t";

  // --- Thông báo lỗi (nếu cần) ---
  public static final String ERROR_MASTER_RETRIEVAL = "Error retrieving master.m3u8";
  public static final String ERROR_VARIANT_RETRIEVAL = "Error retrieving variant playlist for folder {}";
  public static final String ERROR_SEGMENT_RETRIEVAL = "Error retrieving segment file {}/{}";

  /**
   * Lấy file master playlist (master.m3u8) cho HLS.
   *
   * @return ResponseEntity chứa Resource của file master.m3u8 hoặc mã lỗi 404/500
   */
  @GetMapping(ENDPOINT_MASTER)
  ResponseEntity<Resource> getMaster() {
    try {
      Path filePath = Paths.get(HLS_ROOT, "master.m3u8");
      if (!Files.exists(filePath)) {
        return ResponseEntity.notFound().build();
      }
      Resource resource = new FileSystemResource(filePath);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(CONTENT_TYPE_HLS_PLAYLIST));
      return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(ERROR_MASTER_RETRIEVAL, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Lấy variant playlist (playlist.m3u8) từ thư mục được chỉ định.
   * <p>
   * Endpoint: GET /stream/variant?folder=v0
   * </p>
   *
   * @param folder tên thư mục chứa variant (ví dụ: "v0")
   * @return ResponseEntity chứa Resource của file playlist.m3u8 hoặc mã lỗi 404/500
   */
  @GetMapping(ENDPOINT_VARIANT)
  ResponseEntity<Resource> getVariant(@RequestParam("folder") String folder) {
    try {
      Path filePath = Paths.get(HLS_ROOT, folder, "playlist.m3u8");
      if (!Files.exists(filePath)) {
        return ResponseEntity.notFound().build();
      }
      Resource resource = new FileSystemResource(filePath);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(CONTENT_TYPE_HLS_PLAYLIST));
      return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(ERROR_VARIANT_RETRIEVAL, folder, e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Lấy file segment của HLS.
   * <p>
   * Endpoint: GET /stream/{folder}/{seg} (ví dụ: /stream/v0/segment0.ts)
   * </p>
   *
   * @param folder tên thư mục chứa segment (ví dụ: "v0")
   * @param seg    tên file segment (ví dụ: "segment0.ts")
   * @return ResponseEntity chứa Resource của file segment hoặc mã lỗi 404/500
   */
  @GetMapping(ENDPOINT_SEGMENT)
  ResponseEntity<Resource> getSegment(@PathVariable("folder") String folder,
      @PathVariable("seg") String seg) {
    try {
      Path filePath = Paths.get(HLS_ROOT, folder, seg);
      if (!Files.exists(filePath)) {
        return ResponseEntity.notFound().build();
      }
      Resource resource = new FileSystemResource(filePath);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(CONTENT_TYPE_VIDEO_MP2T));
      return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(ERROR_SEGMENT_RETRIEVAL, folder, seg, e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
