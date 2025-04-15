package com.source.animeh.entity.account.auth;

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
@Table(name = "[PASSWORD_RESET_TOKEN]")
public class PasswordResetToken {

  @Id
  @Column(name = "id", length = 36, nullable = false)
  String id;

  @Column(name = "token", length = 100, nullable = false, unique = true)
  String token;

  @Column(name = "expired_at", nullable = false)
  LocalDateTime expiredAt;

  @Column(name = "is_used", nullable = false)
  Boolean isUsed;

  // Liên kết với User
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    if (this.isUsed == null) {
      this.isUsed = false;
    }
  }
}
