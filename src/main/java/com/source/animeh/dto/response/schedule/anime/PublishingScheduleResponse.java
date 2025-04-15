package com.source.animeh.dto.response.schedule.anime;

import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublishingScheduleResponse {

  String id;

  AnimeResponse anime;

  LocalDateTime scheduleDate;
  LocalDateTime modDeadline;
  String statusDeadline;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
