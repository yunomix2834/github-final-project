package com.source.animeh.dto.request.film_series.anime_series;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimeSeriesUpdateRequest {

  String title;
  String description;

  // Nếu muốn thay poster/banner khi update
  MultipartFile posterFile;
  MultipartFile bannerFile;
}
