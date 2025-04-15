package com.source.animeh.dto.request.film_series.episode;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeUpdateRequest {

  String title;
  String description;

  MultipartFile videoFile;
}
