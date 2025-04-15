package com.source.animeh.controller.file;

import com.source.animeh.service.file.AnimeMediaService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu liên quan đến media của Anime.
 * <p>
 * Các endpoint và thông báo được định nghĩa thành hằng số để dễ dàng quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AnimeMediaController.BASE_ENDPOINT)
public class AnimeMediaController {

  // Base endpoint cho media
  public static final String BASE_ENDPOINT = "/public/media";
  // Endpoint cho Anime poster và banner
  public static final String ENDPOINT_ANIME_POSTER = "/anime/{animeId}/poster";
  public static final String ENDPOINT_ANIME_BANNER = "/anime/{animeId}/banner";
  // Endpoint cho Series poster và banner
  public static final String ENDPOINT_SERIES_POSTER = "/series/{seriesId}/poster";
  public static final String ENDPOINT_SERIES_BANNER = "/series/{seriesId}/banner";
  // Endpoint cho HLS master playlist và segment
  public static final String ENDPOINT_HLS_MASTER = "/hls/{storeType}/{seriesId}/{animeId}/{episodeId}/master.m3u8";
  public static final String ENDPOINT_HLS_SEGMENT = "/hls/{storeType}/{seriesId}/{animeId}/{episodeId}/{folderSegment}/{segmentFile}";
  // Hằng số default
  public static final String DEFAULT_SIZE = "original";
  // Các content-type
  public static final String CONTENT_TYPE_MPEGURL = "application/vnd.apple.mpegurl";
  public static final String CONTENT_TYPE_VIDEO_MP2T = "video/mp2t";
  AnimeMediaService animeMediaService;

  /**
   * Lấy file poster của Anime.
   *
   * @param animeId mã định danh của Anime
   * @param size    kích thước poster (mặc định là "original")
   * @return ResponseEntity chứa Resource của file poster
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_ANIME_POSTER)
  ResponseEntity<Resource> getAnimePoster(
      @PathVariable String animeId,
      @RequestParam(defaultValue = DEFAULT_SIZE) String size
  ) throws IOException {
    Path path = animeMediaService.getAnimePosterFile(animeId, size);
    return buildFileResponse(path);
  }

  /**
   * Lấy file banner của Anime.
   *
   * @param animeId mã định danh của Anime
   * @param size    kích thước banner (mặc định là "original")
   * @return ResponseEntity chứa Resource của file banner
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_ANIME_BANNER)
  ResponseEntity<Resource> getAnimeBanner(
      @PathVariable String animeId,
      @RequestParam(defaultValue = DEFAULT_SIZE) String size
  ) throws IOException {
    Path path = animeMediaService.getAnimeBannerFile(animeId, size);
    return buildFileResponse(path);
  }

  /**
   * Lấy file poster của Series.
   *
   * @param seriesId mã định danh của Series
   * @param size     kích thước poster (mặc định là "original")
   * @return ResponseEntity chứa Resource của file poster
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_SERIES_POSTER)
  ResponseEntity<Resource> getSeriesPoster(
      @PathVariable String seriesId,
      @RequestParam(defaultValue = DEFAULT_SIZE) String size
  ) throws IOException {
    Path path = animeMediaService.getSeriesPosterFile(seriesId, size);
    return buildFileResponse(path);
  }

  /**
   * Lấy file banner của Series.
   *
   * @param seriesId mã định danh của Series
   * @param size     kích thước banner (mặc định là "original")
   * @return ResponseEntity chứa Resource của file banner
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_SERIES_BANNER)
  ResponseEntity<Resource> getSeriesBanner(
      @PathVariable String seriesId,
      @RequestParam(defaultValue = DEFAULT_SIZE) String size
  ) throws IOException {
    Path path = animeMediaService.getSeriesBannerFile(seriesId, size);
    return buildFileResponse(path);
  }

  /**
   * Trả về file master.m3u8 của một tập phim.
   *
   * @param storeType loại lưu trữ
   * @param seriesId  mã định danh của Series
   * @param animeId   mã định danh của Anime
   * @param episodeId mã định danh của tập phim
   * @return ResponseEntity chứa Resource của master.m3u8
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_HLS_MASTER)
  ResponseEntity<Resource> getMasterM3u8(
      @PathVariable String storeType,
      @PathVariable String seriesId,
      @PathVariable String animeId,
      @PathVariable String episodeId
  ) throws IOException {
    Path path = animeMediaService.getEpisodeMasterPlaylist(storeType, seriesId, animeId, episodeId);
    if (path == null || !Files.exists(path)) {
      return ResponseEntity.notFound().build();
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(CONTENT_TYPE_MPEGURL));

    Resource resource = new UrlResource(path.toUri());
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  /**
   * Trả về file segment của tập phim (ví dụ: segment .ts).
   *
   * @param storeType     loại lưu trữ
   * @param seriesId      mã định danh của Series
   * @param animeId       mã định danh của Anime
   * @param episodeId     mã định danh của tập phim
   * @param folderSegment thư mục chứa segment
   * @param segmentFile   tên file segment
   * @return ResponseEntity chứa Resource của file segment
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  @GetMapping(ENDPOINT_HLS_SEGMENT)
  ResponseEntity<Resource> getSegment(
      @PathVariable String storeType,
      @PathVariable String seriesId,
      @PathVariable String animeId,
      @PathVariable String episodeId,
      @PathVariable String folderSegment,
      @PathVariable String segmentFile
  ) throws IOException {
    Path path = animeMediaService.getEpisodeSegmentFile(storeType, seriesId, animeId, episodeId,
        folderSegment, segmentFile);
    if (path == null || !Files.exists(path)) {
      return ResponseEntity.notFound().build();
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(CONTENT_TYPE_VIDEO_MP2T));
    Resource resource = new UrlResource(path.toUri());
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  /**
   * Xây dựng phản hồi cho file dựa trên Path truyền vào.
   *
   * @param path đường dẫn đến file
   * @return ResponseEntity chứa Resource của file nếu tồn tại, hoặc trả về 404 nếu không tồn tại
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  ResponseEntity<Resource> buildFileResponse(Path path) throws IOException {
    if (path == null || !Files.exists(path)) {
      return ResponseEntity.notFound().build();
    }
    Resource resource = new UrlResource(path.toUri());
    MediaType mediaType = detectMediaType(path);
    return ResponseEntity.ok()
        .contentType(mediaType)
        .body(resource);
  }

  /**
   * Phát hiện mime-type của file dựa trên đường dẫn.
   *
   * @param path đường dẫn đến file
   * @return MediaType của file, nếu không xác định được sẽ trả về "application/octet-stream"
   * @throws IOException nếu có lỗi trong quá trình đọc file
   */
  MediaType detectMediaType(Path path) throws IOException {
    String probe = Files.probeContentType(path);
    if (probe == null) {
      probe = "application/octet-stream";
    }
    return MediaType.parseMediaType(probe);
  }
}
