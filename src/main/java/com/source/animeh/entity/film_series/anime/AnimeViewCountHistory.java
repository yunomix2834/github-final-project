package com.source.animeh.entity.film_series.anime;

import com.source.animeh.entity.film_series.Anime;
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
@Table(name = "[ANIME_VIEW_COUNT_HISTORY]")
public class AnimeViewCountHistory {

  @Id
  @Column(name = "id", length = 36, nullable = false)
  String id;

  @ManyToOne
  @JoinColumn(name = "anime_id", nullable = false)
  Anime anime;

  @Column(name = "total_view", nullable = false)
  Long totalView;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
