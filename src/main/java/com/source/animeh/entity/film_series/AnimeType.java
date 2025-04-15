package com.source.animeh.entity.film_series;

import com.source.animeh.data.AnimeTypeId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "[ANIME_TYPE]")
@IdClass(AnimeTypeId.class)
public class AnimeType {

  @Id
  @ManyToOne
  @JoinColumn(name = "anime_id", updatable = false, nullable = false)
  Anime anime;

  @Id
  @ManyToOne
  @JoinColumn(name = "type_id", updatable = false, nullable = false)
  Type type;
}
