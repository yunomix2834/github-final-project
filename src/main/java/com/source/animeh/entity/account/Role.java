package com.source.animeh.entity.account;

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
@Table(name = "[ROLE]")
public class Role {

  @Id
  @Column(name = "id", updatable = false, nullable = false, length = 36)
  String id;

  @Column(name = "name", nullable = false, length = 50, unique = true)
  String name; // "ROLE_USER" / "ROLE_MODERATOR" / "ROLE_ADMIN"

  @Column(name = "description", length = 255)
  String description;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @OneToMany(mappedBy = "role")
  List<User> users;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}



