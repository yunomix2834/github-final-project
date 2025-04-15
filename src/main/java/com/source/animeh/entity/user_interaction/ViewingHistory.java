package com.source.animeh.entity.user_interaction;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "[VIEWING_HISTORY]")
public class ViewingHistory {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // FK -> User
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  // FK -> Episode
  @ManyToOne
  @JoinColumn(name = "episode_id", nullable = false)
  Episode episode;

  @Column(name = "watched_date", nullable = false)
  LocalDateTime watchedDate;

  @Column(name = "watched_duration", precision = 10, scale = 2)
  BigDecimal watchedDuration;

  @Column(name = "is_deleted")
  Boolean isDeleted;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
