package com.source.animeh.dto.response.film_series.anime;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublishingScheduleInAnimeResponse {

  String id;
  
  LocalDateTime scheduleDate;
  LocalDateTime modDeadline;
  String statusDeadline;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
