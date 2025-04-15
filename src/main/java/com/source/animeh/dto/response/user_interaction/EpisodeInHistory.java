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
public class EpisodeInHistory {

  String id;
  Integer episodeNumber;
  String videoUrl;
  BigDecimal duration;

  // anime
  AnimeInHistory anime;
}
