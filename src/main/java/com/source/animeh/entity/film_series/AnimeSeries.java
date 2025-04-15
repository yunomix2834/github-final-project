package com.source.animeh.entity.film_series;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "[ANIME_SERIES]")
public class AnimeSeries {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "title", length = 255, nullable = false)
  String title;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "poster_url", length = 255)
  String posterUrl;

  @Column(name = "banner_url", length = 255)
  String bannerUrl;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  // 1-n với Anime
  @OneToMany(mappedBy = "series")
  List<Anime> animes;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
