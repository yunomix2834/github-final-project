package com.source.animeh.service.video;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FfmpegVideoService {

  private static final String INPUT_PATH = "F:\\fileHoc\\animeh\\video_test\\input_video.mp4";

  private static final String OUTPUT_FOLDER = "F:\\fileHoc\\animeh\\video_test";

  public void generateMultiResolutionHls()
      throws IOException, InterruptedException {
    String[] command = {
        "ffmpeg",
        "-i", INPUT_PATH,
        "-filter_complex",
        "[0:v]split=6[v144][v240][v360][v480][v720][v1080]; "
            + "[v1080]scale=w=1920:h=1080:force_original_aspect_ratio=decrease[v1080out]; "
            + "[v720]scale=w=1280:h=720:force_original_aspect_ratio=decrease[v720out]; "
            + "[v480]scale=w=854:h=480:force_original_aspect_ratio=decrease,scale=trunc(iw/2)*2:trunc(ih/2)*2[v480out]; "
            + "[v360]scale=w=640:h=360:force_original_aspect_ratio=decrease[v360out]; "
            + "[v240]scale=w=426:h=240:force_original_aspect_ratio=decrease[v240out]; "
            + "[v144]scale=w=256:h=144:force_original_aspect_ratio=decrease[v144out]",
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
        "-hls_segment_filename", OUTPUT_FOLDER + "\\v%v\\segment%d.ts",
        OUTPUT_FOLDER + "\\v%v\\playlist.m3u8"
    };

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.inheritIO(); // để ffmpeg log ra console
    Process process = pb.start();

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("Process exited with code " + exitCode);
    }
    log.info("Transcode completed. Master playlist is at: {}/master.m3u8", OUTPUT_FOLDER);
  }
}
