package com.source.animeh.dto.response.episode;

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
public class EpisodeResponse {

  // Các thông tin của Episode
  String id;
  AnimeInEpisodeResponse anime;

  Integer episodeNumber;
  String title;
  String description;
  String videoUrl;
  String subtitleUrl;
  BigDecimal duration;
  LocalDateTime scheduledDate;
  LocalDateTime publishedAt;

  BigDecimal historyDuration;

  EpisodePublishingScheduleInEpisodeResponse episodePublishingSchedule;

  // Dành cho Admin/Mod
  String status;              // ẩn với user
  String rejectedReason;      // ẩn với user
  String submittedBy;         // ẩn với user
  String reviewedBy;          // ẩn với user
  LocalDateTime submittedAt;  // ẩn với user
  LocalDateTime reviewedAt;   // ẩn với user
  LocalDateTime createdAt;    // ẩn với user
  LocalDateTime updatedAt;    // ẩn với user
}
