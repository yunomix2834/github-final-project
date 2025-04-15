package com.source.animeh.dto.response.film_series.anime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodeInAnimeResponse {

  String id;
  Integer episodeNumber;
  String title;
  String videoUrl;
  String subtitleUrl;
  BigDecimal duration;
  LocalDateTime scheduledDate;
  LocalDateTime publishedAt;

  BigDecimal historyDuration;

  // Chỉ hiển thị cho Moderator/Admin
  String status;
  String rejectedReason;
  String submittedBy;
  String reviewedBy;
  LocalDateTime submittedAt;
  LocalDateTime reviewedAt;
}
