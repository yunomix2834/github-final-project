package com.source.animeh.entity.store_transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "[STORE_PRODUCT]")
public class StoreProduct {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "name", length = 255, nullable = false)
  String name;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "price_in_GP", nullable = false)
  Integer priceInGP;

  @Column(name = "duration", nullable = false)
  Integer duration;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
