package com.source.animeh.dto.request.film_series.anime_series;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimeSeriesCreateRequest {

  String title;
  String description;

  // Có thể upload poster/banner ngay khi tạo
  MultipartFile posterFile;
  MultipartFile bannerFile;
}
