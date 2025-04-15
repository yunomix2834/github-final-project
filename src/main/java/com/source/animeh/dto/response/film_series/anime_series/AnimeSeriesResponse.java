package com.source.animeh.dto.response.film_series.anime_series;

import com.source.animeh.dto.response.film_series.anime.AnimeInSeriesResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimeSeriesResponse {

  String id;
  String title;
  String description;
  String posterUrl;
  String bannerUrl;

  LocalDateTime createdAt;  // Moderator
  LocalDateTime updatedAt;  // Moderator

  // Danh sách Anime con
  List<AnimeInSeriesResponse> animes;
}
