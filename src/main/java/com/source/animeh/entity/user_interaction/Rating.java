package com.source.animeh.entity.user_interaction;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
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
@Table(name = "[RATING]")
public class Rating {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // FK -> Anime
  @ManyToOne
  @JoinColumn(name = "anime_id", nullable = false)
  Anime anime;

  // FK -> User
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "value", precision = 2, scale = 0, nullable = false)
  BigDecimal value; // Thang 10 (1..10)

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
