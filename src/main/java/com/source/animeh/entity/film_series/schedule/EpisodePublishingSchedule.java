package com.source.animeh.entity.film_series.schedule;

import com.source.animeh.entity.episode.Episode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "[EPISODE_PUBLISHING_SCHEDULE]")
public class EpisodePublishingSchedule {

  @Id
  @Column(length = 36, nullable = false)
  String id;

  @OneToOne
  @JoinColumn(name = "episode_id")
  Episode episode;

  @Column(name = "schedule_type", length = 50)
  String scheduleType;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "schedule_date")
  LocalDateTime scheduleDate; // Thời điểm "ra mắt" episode

  @Column(name = "mod_deadline")
  LocalDateTime modDeadline; // Thời hạn Mod phải duyệt

  // Status tuỳ ý: ON_TIME, DELAY, EXPIRED, ...
  @Column(name = "status_deadline")
  String statusDeadline;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
