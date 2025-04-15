package com.source.animeh.entity.account;

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
@Table(name = "[MODERATOR_REQUEST]")
public class ModeratorRequest {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // Người request (FK -> USER)
  @ManyToOne
  @JoinColumn(name = "user_id")
  User user;

  // Admin xử lý request (FK -> USER)
  @ManyToOne
  @JoinColumn(name = "admin_id")
  User admin;

  @Column(name = "request_date", nullable = false)
  LocalDateTime requestDate;

  @Column(name = "status", length = 50, nullable = false)
  String status;

  @Column(name = "comment_rejected", length = 255)
  String commentRejected;

  @Column(name = "processed_at")
  LocalDateTime processedAt;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
