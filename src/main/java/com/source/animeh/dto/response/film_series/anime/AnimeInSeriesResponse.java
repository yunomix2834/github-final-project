package com.source.animeh.dto.response.film_series.anime;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimeInSeriesResponse {

  String id;
  String title;
  String seriesOrder;

  String posterUrl;
  String bannerUrl;
  BigDecimal averageRating;
  Long viewCount;

  boolean inWatchlist;
}
