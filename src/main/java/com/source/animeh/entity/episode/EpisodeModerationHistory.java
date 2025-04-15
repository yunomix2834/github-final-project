package com.source.animeh.entity.episode;

import com.source.animeh.entity.account.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "[EPISODE_MODERATION_HISTORY]")
public class EpisodeModerationHistory {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "episode_id", nullable = false)
  String episodeId;

  @Column(name = "action", length = 50, nullable = false)
  String action;

  @Column(name = "reason", length = 255)
  String reason;

  @ManyToOne
  @JoinColumn(name = "action_by", nullable = false)
  User actionBy;

  @Column(name = "action_at", nullable = false)
  LocalDateTime actionAt;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
