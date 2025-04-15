package com.source.animeh.dto.request.film_series.episode;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeCreateRequest {

  String animeId;     // Bắt buộc
  Integer episodeNumber;
  String title;
  String description;

  // videoFile (bắt buộc) -> transcode
  MultipartFile videoFile;

  LocalDateTime scheduledDate;
}
