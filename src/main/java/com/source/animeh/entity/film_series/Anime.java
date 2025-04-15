package com.source.animeh.entity.film_series;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
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
@Table(name = "[ANIME]")
public class Anime {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "title", length = 255, nullable = false)
  String title;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "release_year")
  Integer releaseYear;

  @Column(name = "director", length = 100)
  String director;

  @Column(name = "total_episodes")
  Integer totalEpisodes;

  @Column(name = "expected_episodes")
  Integer expectedEpisodes;

  @Column(name = "poster_url", length = 255)
  String posterUrl;

  @Column(name = "banner_url", length = 255)
  String bannerUrl;

  @Column(name = "trailer_url", length = 255)
  String trailerUrl;

  @Column(name = "status_submitted", length = 50, nullable = false)
  String statusSubmitted;

  @Column(name = "rejected_reason", length = 255)
  String rejectedReason;

  @Column(name = "rating_count")
  Integer ratingCount;

  @Column(name = "sum_rating", precision = 10, scale = 2)
  BigDecimal sumRating;

  @Column(name = "next_episode_publishing_date")
  LocalDateTime nextEpisodePublishingDate;

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

  @Column(name = "average_rating", precision = 3, scale = 1, nullable = false)
  BigDecimal averageRating;

  @Column(name = "view_count")
  Long viewCount;

  // FK -> ANIME_SERIES
  @ManyToOne
  @JoinColumn(name = "series_id")
  AnimeSeries series;

  @Column(name = "series_order", length = 100, nullable = false)
  String seriesOrder;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  // n-n với Type (thông qua AnimeType)
  @Builder.Default
  @OneToMany(mappedBy = "anime")
  List<AnimeType> animeTypes = new ArrayList<>();

  // 1-n với Episode
  @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Episode> episodes;

  //    // 1-n với Comment
//    @OneToMany(mappedBy = "anime")
//    List<Comment> comments;
//
//    // 1-n với Rating
//    @OneToMany(mappedBy = "anime")
//    List<Rating> ratings;
//
//    // 1-n với Watchlist
//    @OneToMany(mappedBy = "anime")
//    List<Watchlist> watchlists;
//
//    // 1-n với UserSetting
//    @OneToMany(mappedBy = "anime")
//    List<UserSetting> userSettings;
//
  // 1-n với AnimeModerationHistory
  @OneToMany(mappedBy = "animeId", cascade = CascadeType.ALL, orphanRemoval = true)
  List<AnimeModeratorHistory> moderationHistories = new ArrayList<>();

  @OneToOne(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
  PublishingSchedule publishingSchedule;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
