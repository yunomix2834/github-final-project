package com.source.animeh.dto.request.film_series.anime;

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
public class AnimeUpdateRequest {

  String title;
  String description;
  Integer releaseYear;
  String director;
  Integer expectedEpisodes;
  String trailerUrl;
  String seriesOrder;

  LocalDateTime nextEpisodePublishingDate;

  MultipartFile posterFile;
  MultipartFile bannerFile;

  List<String> movieTypes;
  List<String> statusNames;
  List<String> releaseYearNames;
  List<String> studioNames;
  List<String> scheduleNames;
  List<String> genreNames;
  List<String> countryNames;

  List<String> typeIds;
}
