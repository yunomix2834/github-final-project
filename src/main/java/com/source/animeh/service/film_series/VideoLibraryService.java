package com.source.animeh.service.film_series;

import com.source.animeh.interface_package.service.film_series.VideoLibraryServiceInterface;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Quản lý thư mục video: LIBRARY_ROOT/seriesId/animeId/episodeId/... Tự động transcode HLS khi
 * upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoLibraryService implements VideoLibraryServiceInterface {

  // Thư mục gốc chứa tất cả các video library
  // TODO Chuyển thành biến trong application.properties
  static String LIBRARY_ROOT = "F:\\fileHoc\\animeh\\video_library";

//    /**
//     * Tạo folder series
//     */
//    public void createSeriesFolder(String seriesId) {
//
//        // Lấy đường dẫn
//        String seriesPath = Paths.get(LIBRARY_ROOT, seriesId).toString();
//        File folder = new File(seriesPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//            log.info("Created series folder at {}", seriesPath);
//        }
//    }

  // Tạo folder: unapproved/<seriesId>/<animeId>
  @Override
  public void createAnimeFolder(
      String storeType,
      String seriesId,
      String animeId) {
    String path = Paths.get(
        LIBRARY_ROOT,
        storeType,
        seriesId,
        animeId
    ).toString();
    File f = new File(path);
    if (!f.exists()) {
      f.mkdirs();
    }
    log.info("Created anime folder: {}", path);
  }

  /**
   * Transcode 1 episode => tạo folder episode Output:
   * LIBRARY_ROOT/storeType/<seriesId>/<animeId>/<episodeId>/
   */
  @Override
  public void transcodeEpisodeHls(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId,
      String inputFile)
      throws IOException, InterruptedException {

    // Tạo folder
    String outputDir = Paths.get(
        LIBRARY_ROOT,
        storeType,
        seriesId,
        animeId,
        episodeId
    ).toString();
    File folder = new File(outputDir);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // Tạo lệnh ffmpeg
    // => output master.m3u8, v0, v1,... trong <episodeId> folder
    String[] command = {
        "ffmpeg",
        "-i", inputFile,
        "-filter_complex",
        "[0:v]split=6[v144][v240][v360][v480][v720][v1080]; " +
            "[v1080]scale=w=1920:h=1080:force_original_aspect_ratio=decrease[v1080out]; " +
            "[v720]scale=w=1280:h=720:force_original_aspect_ratio=decrease[v720out]; " +
            "[v480]scale=w=854:h=480:force_original_aspect_ratio=decrease,scale=trunc(iw/2)*2:trunc(ih/2)*2[v480out]; "
            +
            "[v360]scale=w=640:h=360:force_original_aspect_ratio=decrease[v360out]; " +
            "[v240]scale=w=426:h=240:force_original_aspect_ratio=decrease[v240out]; " +
            "[v144]scale=w=256:h=144:force_original_aspect_ratio=decrease[v144out]",
        "-map", "[v144out]",
        "-map", "0:a",
        "-c:v:0", "libx264",
        "-profile:v:0", "baseline",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:0", "300k",
        "-maxrate:v:0", "321k",
        "-bufsize:v:0", "600k",

        "-map", "[v240out]",
        "-map", "0:a",
        "-c:v:1", "libx264",
        "-profile:v:1", "baseline",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:1", "700k",
        "-maxrate:v:1", "749k",
        "-bufsize:v:1", "1200k",

        "-map", "[v360out]",
        "-map", "0:a",
        "-c:v:2", "libx264",
        "-profile:v:2", "main",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:2", "1000k",
        "-maxrate:v:2", "1050k",
        "-bufsize:v:2", "1500k",

        "-map", "[v480out]",
        "-map", "0:a",
        "-c:v:3", "libx264",
        "-profile:v:3", "main",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:3", "1500k",
        "-maxrate:v:3", "1575k",
        "-bufsize:v:3", "2100k",

        "-map", "[v720out]",
        "-map", "0:a",
        "-c:v:4", "libx264",
        "-profile:v:4", "main",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:4", "2500k",
        "-maxrate:v:4", "2625k",
        "-bufsize:v:4", "3500k",

        "-map", "[v1080out]",
        "-map", "0:a",
        "-c:v:5", "libx264",
        "-profile:v:5", "high",
        "-crf", "20",
        "-sc_threshold", "0",
        "-g", "48",
        "-keyint_min", "48",
        "-b:v:5", "4000k",
        "-maxrate:v:5", "4200k",
        "-bufsize:v:5", "5600k",

        "-c:a", "aac",
        "-b:a", "128k",
        "-f", "hls",
        "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3 v:4,a:4 v:5,a:5",
        "-master_pl_name", "master.m3u8",
        "-hls_time", "6",
        "-hls_list_size", "0",
        "-hls_segment_filename", outputDir + "\\v%v\\segment%d.ts",
        outputDir + "\\v%v\\playlist.m3u8"
    };

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.inheritIO();
    Process process = pb.start();
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("FFmpeg fail, exit code " + exitCode);
    }
    log.info("Transcoded input: {} => folder: {}",
        inputFile, outputDir);
  }

  /**
   * Trả về URL => /hls/{storeType}/{seriesId}/{animeId}/{episodeId}/master.m3u8
   */
  // TODO trả ra mã lỗi trong ErrorCode
  @Override
  public String getEpisodeMasterUrl(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId) {
    return "/hls/" + storeType + "/" + seriesId + "/" + animeId + "/" + episodeId + "/master.m3u8";
  }

  // Di chuyển Anime folder => unapproved -> approved
  @Override
  public void moveAnimeFolder(
      String fromStore,
      String toStore,
      String seriesId,
      String animeId) {
    String src = Paths.get(
        LIBRARY_ROOT,
        fromStore,
        seriesId,
        animeId
    ).toString();
    String dst = Paths.get(
        LIBRARY_ROOT,
        toStore,
        seriesId,
        animeId
    ).toString();

    File s = new File(src), d = new File(dst);

//        if (!s.exists()) {
//            throw new IOException("Source folder does not exist: " + src);
//        }

    // Tạo folder parent cho d => .../toStore/seriesId
    File dParent = d.getParentFile();
    if (!dParent.exists()) {
      dParent.mkdirs(); // Tạo parent folder
    }

//        if (d.exists()) {
//            throw new IOException("Destination folder already exist: " + dst);
//        }

    s.renameTo(d);
//        boolean success = s.renameTo(d);
//        if (!success) {
//            throw new IOException("Cannot rename from " + src + " to " + dst);
//        }
    log.info("Moved {} => {}", src, dst);
  }

  // Di chuyển Episode folder => unapproved -> approved
  @Override
  public void moveEpisodeFolder(
      String fromStore,
      String toStore,
      String seriesId,
      String animeId,
      String episodeId) {
    String src = Paths.get(
        LIBRARY_ROOT,
        fromStore,
        seriesId,
        animeId,
        episodeId
    ).toString();
    String dst = Paths.get(
        LIBRARY_ROOT,
        toStore,
        seriesId,
        animeId,
        episodeId
    ).toString();

    File s = new File(src), d = new File(dst);

//        if (!s.exists()){
//            log.info("Source folder does not exist: " + src);
//            throw new IOException("Source does not exist");
//        }

    // Tạo folder parent cho d => .../toStore/seriesId
    File dParent = d.getParentFile();
    if (!dParent.exists()) {
      dParent.mkdirs(); // Tạo parent folder
    }

//        if (d.exists()){
//            log.info("Destination folder already exist: " + dst);
//            throw new IOException("Destination exist");
//        }

    s.renameTo(d);
//        boolean success = s.renameTo(d);
//        if (!success){
//            throw new IOException("Cannot rename from " + src + " to " + dst);
//        }
    log.info("Moved {} => {}", src, dst);
  }

  // Xoá folder  => unapproved/<seriesId>/<animeId>/(<episodeId>)
  @Override
  public void deleteFolder(
      String storeType,
      String... pathElems)
      throws IOException {
    String p = Paths.get(
        LIBRARY_ROOT,
        storeType,
        Paths.get("", pathElems).toString()
    ).toString();
    File f = new File(p);
    if (f.exists()) {
      deleteRecursively(f);
    }
  }

  void deleteRecursively(File file) throws IOException {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File c : children) {
          deleteRecursively(c);
        }
      }
    }

    // Thử xóa lần 1
    boolean deleted = file.delete();
    if (!deleted) {
      // Thử chờ 1 chút (500ms) rồi xóa lại
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      // Thử xóa lần 2
      deleted = file.delete();
      if (!deleted) {
        // Vẫn thất bại => quăng lỗi
        throw new IOException("Cannot delete: " + file.getAbsolutePath());
      }
    }
  }
}
