package com.source.animeh.dto.request.film_series.anime.schedule;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateScheduleRequest {

  String animeId;
  LocalDateTime scheduleDate;
  String description;
}
