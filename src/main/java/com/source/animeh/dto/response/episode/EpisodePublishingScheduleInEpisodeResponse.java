package com.source.animeh.dto.response.episode;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodePublishingScheduleInEpisodeResponse {

  String id;

  LocalDateTime scheduleDate;
  LocalDateTime modDeadline;
  String statusDeadline;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
