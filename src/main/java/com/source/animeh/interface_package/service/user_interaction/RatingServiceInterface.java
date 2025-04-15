package com.source.animeh.interface_package.service.user_interaction;

import java.math.BigDecimal;

public interface RatingServiceInterface {

  // Rating Anime
  BigDecimal rateAnime(String animeId, BigDecimal value);
}
