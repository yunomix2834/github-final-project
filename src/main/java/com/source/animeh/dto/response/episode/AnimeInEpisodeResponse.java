package com.source.animeh.dto.response.episode;

import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.TypeItemResponse;
import java.math.BigDecimal;
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
public class AnimeInEpisodeResponse {

  String id;
  String title;
  Integer releaseYear;
  String director;
  Integer totalEpisodes;
  Integer expectedEpisodes;
  String posterUrl;
  String bannerUrl;
  String trailerUrl;

  LocalDateTime nextEpisodePublishingDate;

  BigDecimal averageRating;
  Long viewCount;
  String seriesOrder;

  boolean inWatchlist;

  // Danh sách Type
  List<TypeItemResponse> typeItems;

  // Series (rút gọn)
  AnimeSeriesInAnimeResponse series;
}
