package com.source.animeh.dto.request.film_series.anime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimeCreateRequest {

  String seriesId;       // optional, nếu null -> tạo series mới
  String title;          // required
  String description;    // optional

  LocalDateTime nextEpisodePublishingDate;

  // Upload poster/banner ngay khi tạo
  MultipartFile posterFile;
  MultipartFile bannerFile;

  Integer releaseYear;
  Integer expectedEpisodes;
  String linkTrailer;

  // Danh sách typeId do user chọn thêm (nếu có)
  List<String> typeIds;

  // Thêm 3 trường mới:
  // 1) Nhập tên quốc gia => gắn type "Quốc gia"
  String countryName;
  // 2) Nhập tên studio => gắn type "Studio"
  String studioName;
  // 3) Nhập 1 ngày (VD: 2025-05-07) => Để suy ra "Thứ tư"
  LocalDate scheduleDate;
}
