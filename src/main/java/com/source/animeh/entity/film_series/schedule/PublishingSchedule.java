package com.source.animeh.entity.film_series.schedule;

import com.source.animeh.entity.film_series.Anime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "[PUBLISHING_SCHEDULE]")
public class PublishingSchedule {

  @Id
  String id;

  @OneToOne
  @JoinColumn(name = "anime_id")
  Anime anime;

  @Column(name = "schedule_type", length = 50)
  String scheduleType;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "schedule_date")
  LocalDateTime scheduleDate; // ngày admin phải duyệt

  @Column(name = "mod_deadline")
  LocalDateTime modDeadline;  // scheduleDate - 1

  @Column(name = "status_deadline")
  String statusDeadline; // "OK", "QUA_HAN_MOD", "QUA_HAN_ADMIN", ...

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
