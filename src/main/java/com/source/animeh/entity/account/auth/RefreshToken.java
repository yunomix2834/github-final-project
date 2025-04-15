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
@Table(name = "[REFRESH_TOKEN]")
public class RefreshToken {

  @Id
  @Column(length = 36, nullable = false)
  String id;

  // Mối quan hệ 1-n: 1 User có thể có nhiều refresh token (vd login từ nhiều devices)
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "token", length = 200, unique = true, nullable = false)
  String token;

  @Column(name = "expired_at", nullable = false)
  LocalDateTime expiredAt;

  @Column(name = "is_revoked", nullable = false)
  Boolean isRevoked;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @PrePersist
  void onCreate() {
    createdAt = LocalDateTime.now();
    if (isRevoked == null) {
      isRevoked = false;
    }
  }
}
