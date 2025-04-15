package com.source.animeh.dto.response.film_series.anime;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimeSeriesInAnimeResponse {

  String id;
  String title;
  String posterUrl;
  String bannerUrl;
}
