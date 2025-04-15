package com.source.animeh.controller.video;

import com.source.animeh.service.video.FfmpegVideoService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý chuyển mã video sử dụng Ffmpeg.
 * <p>
 * Phương thức trong controller này thực hiện chuyển mã video thành đa độ phân giải (HLS) và thông
 * báo kết quả thông qua master.m3u8 và các thư mục tương ứng. Các endpoint, thông báo thành công và
 * lỗi được định nghĩa dưới dạng hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(VideoFfmpegController.BASE_ENDPOINT)
public class VideoFfmpegController {

  // --- Hằng số Endpoint ---
  public static final String BASE_ENDPOINT = "/video";
  public static final String ENDPOINT_GENERATE = "/generate";
  // --- Hằng số Thông báo ---
  public static final String MSG_TRANSCODE_DONE = "Transcode done, check master.m3u8 + v0..v5 folders!";
  public static final String MSG_ERROR_PREFIX = "Error: ";
  FfmpegVideoService ffmpegVideoService;

  /**
   * Thực hiện chuyển mã video sang định dạng HLS với nhiều độ phân giải.
   * <p>
   * Khi chuyển mã thành công, trả về thông báo thành công và chỉ dẫn kiểm tra file master.m3u8 cùng
   * các thư mục chứa video đã chuyển mã. Nếu có lỗi, trả về mã lỗi 500 kèm theo thông báo lỗi.
   * </p>
   *
   * @return ResponseEntity chứa thông báo kết quả chuyển mã
   */
  @PostMapping(ENDPOINT_GENERATE)
  ResponseEntity<?> generate() {
    try {
      ffmpegVideoService.generateMultiResolutionHls();
      return ResponseEntity.ok(MSG_TRANSCODE_DONE);
    } catch (Exception e) {
      log.error("Error during video transcoding", e);
      return ResponseEntity.internalServerError().body(MSG_ERROR_PREFIX + e.getMessage());
    }
  }
}
