package com.source.animeh.entity.episode;

import com.source.animeh.data.UserSettingId;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "[USER_SETTING]")
@IdClass(UserSettingId.class)
public class UserSetting {

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id", updatable = false, nullable = false)
  User user;

  @Id
  @ManyToOne
  @JoinColumn(name = "anime_id", updatable = false, nullable = false)
  Anime anime;

  @Column(name = "auto_play_next", nullable = false)
  Boolean autoPlayNext;

  @Column(name = "auto_play_next_duration", precision = 10, scale = 2, nullable = false)
  BigDecimal autoPlayNextDuration;

  @Column(name = "skip_opening", nullable = false)
  Boolean skipOpening;

  @Column(name = "skip_opening_duration", precision = 5, scale = 2, nullable = false)
  BigDecimal skipOpeningDuration;

  @Column(name = "night_mode", nullable = false)
  Boolean nightMode;

  @Column(name = "zoom_on_play", nullable = false)
  Boolean zoomOnPlay;

  @Column(name = "default_quality", length = 10, nullable = false)
  String defaultQuality;

  @Column(name = "default_speed", precision = 3, scale = 1, nullable = false)
  BigDecimal defaultSpeed;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
