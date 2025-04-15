package com.source.animeh.entity.notification;

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
@Table(name = "[NOTIFICATION]")
public class Notification {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // sender_id -> FK -> User
  @ManyToOne
  @JoinColumn(name = "sender_id")
  User sender;

  // receiver_id -> FK -> User
  @ManyToOne
  @JoinColumn(name = "receiver_id")
  User receiver;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  // Chứa các thông tin khác dạng JSON
  // title, message, time, type, target_url, target_type, target_reference, is_read, read_at
  @Column(name = "json_data", columnDefinition = "NVARCHAR(MAX)", nullable = false)
  String jsonData;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
