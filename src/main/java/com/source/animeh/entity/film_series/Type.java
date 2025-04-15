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
@Table(name = "[TYPE]")
public class Type {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "name", length = 100, nullable = false, unique = true)
  String name;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "type", length = 100, nullable = false)
  String type;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  // n-n với Anime (thông qua AnimeType)
  @OneToMany(mappedBy = "type")
  List<AnimeType> animeTypes;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
