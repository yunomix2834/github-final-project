package com.source.animeh.dto.response.user_interaction;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class WatchlistResponse {

  String id;
  LocalDateTime dateAdded;

  AnimeInWatchlist anime;  // Thông tin anime
}
