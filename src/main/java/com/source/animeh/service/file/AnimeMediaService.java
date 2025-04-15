package com.source.animeh.service.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Builder
public class AnimeMediaService {

  static String VIDEO_ROOT = "F:/fileHoc/animeh/video_library";

  static String IMAGE_ROOT = "F:/fileHoc/animeh/image_library";

  /**
   * Lấy file poster của Anime (theo size = tiny|small|original). Ví dụ path:
   * F:/fileHoc/animeh/image_library/anime/{animeId}/poster/original.png
   */
  public Path getAnimePosterFile(String animeId, String size) {
    return findSizedImagePath(
        Paths.get(IMAGE_ROOT, "anime", animeId, "poster"),
        size
    );
  }

  /**
   * Lấy file banner của Anime (theo size = tiny|small|original).
   */
  public Path getAnimeBannerFile(String animeId, String size) {
    return findSizedImagePath(
        Paths.get(IMAGE_ROOT, "anime", animeId, "banner"),
        size
    );
  }

  /**
   * Lấy file poster của Series
   */
  public Path getSeriesPosterFile(String seriesId, String size) {
    return findSizedImagePath(
        Paths.get(IMAGE_ROOT, "series", seriesId, "poster"),
        size
    );
  }

  /**
   * Lấy file banner của Series
   */
  public Path getSeriesBannerFile(String seriesId, String size) {
    return findSizedImagePath(
        Paths.get(IMAGE_ROOT, "series", seriesId, "banner"),
        size
    );
  }

  /**
   * Lấy file master.m3u8 của 1 Episode (storeType = unapproved/approved).
   */
  public Path getEpisodeMasterPlaylist(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId
  ) {
    // VD: F:/fileHoc/animeh/video_library/approved/{seriesId}/{animeId}/{episodeId}/master.m3u8
    return Paths.get(
        VIDEO_ROOT,
        storeType,
        seriesId,
        animeId,
        episodeId,
        "master.m3u8"
    );
  }

  /**
   * Lấy file segment .ts, ví dụ /v0/segment0.ts
   */
  public Path getEpisodeSegmentFile(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId,
      String folderSegment,      // "v0"
      String segmentFileName     // "segment0.ts"
  ) {
    // => .../video_library/approved/seriesId/animeId/episodeId/v0/segment0.ts
    return Paths.get(
        VIDEO_ROOT,
        storeType,
        seriesId,
        animeId,
        episodeId,
        folderSegment,
        segmentFileName
    );
  }

  /**
   * Tìm file ảnh có prefix = {size}. Ví dụ: "original.jpg", "tiny.png", "small.jpeg"
   */
  // TODO thêm mã lỗi trả ra
  Path findSizedImagePath(Path folder, String size) {
    if (!Files.isDirectory(folder)) {
      return null;
    }
    try {
      return Files.list(folder)
          .filter(p -> p.getFileName().toString().startsWith(size + "."))
          .findFirst()
          .orElse(null);
    } catch (Exception e) {
      log.error("Cannot list folder: {}", folder, e);
      return null;
    }
  }
}
