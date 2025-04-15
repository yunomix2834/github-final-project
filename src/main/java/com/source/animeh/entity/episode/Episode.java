package com.source.animeh.entity.episode;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "[EPISODE]")
public class Episode {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // FK -> Anime
  @ManyToOne
  @JoinColumn(name = "anime_id", nullable = false)
  Anime anime;

  @Column(name = "episode_number", nullable = false)
  Integer episodeNumber;

  @Column(name = "title", length = 255)
  String title;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "video_url", length = 255, nullable = false)
  String videoUrl;

  @Column(name = "subtitle_url", length = 255)
  String subtitleUrl;

  @Column(name = "duration", precision = 10, scale = 2)
  BigDecimal duration;

  @Column(name = "scheduled_date", nullable = false)
  LocalDateTime scheduledDate;

  @Column(name = "published_at")
  LocalDateTime publishedAt;

  @Column(name = "status", length = 50, nullable = false)
  String status;

  @Column(name = "rejected_reason", length = 255)
  String rejectedReason;

  // submitted_by -> FK -> User
  @ManyToOne
  @JoinColumn(name = "submitted_by", nullable = false)
  User submittedBy;

  // reviewed_by -> FK -> User
  @ManyToOne
  @JoinColumn(name = "reviewed_by")
  User reviewedBy;

  @Column(name = "submitted_at", nullable = false)
  LocalDateTime submittedAt;

  @Column(name = "reviewed_at")
  LocalDateTime reviewedAt;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  //    // 1-n với ViewingHistory
//    @OneToMany(mappedBy = "episode")
//    List<ViewingHistory> viewingHistories;
//
  // 1-n với EpisodeModerationHistory
  @OneToMany(mappedBy = "episodeId", cascade = CascadeType.ALL, orphanRemoval = true)
  List<EpisodeModerationHistory> moderationHistories = new ArrayList<>();

  @OneToOne(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
  EpisodePublishingSchedule episodeSchedule;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
