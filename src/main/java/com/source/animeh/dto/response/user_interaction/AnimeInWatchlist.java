package com.source.animeh.dto.response.user_interaction;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimeInWatchlist {

  String id;
  String title;
  String description;
  Integer totalEpisodes;
  Long viewCount;
  BigDecimal averageRating;
  String posterUrl;
  String bannerUrl;
}
