package com.source.animeh.entity.store_transaction;

import com.source.animeh.entity.account.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "[USER_PURCHASE]")
public class UserPurchase {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // user_id -> FK -> User
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  // product_id -> FK -> StoreProduct
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  StoreProduct product;

  @Column(name = "purchase_date", nullable = false)
  LocalDateTime purchaseDate;

  @Column(name = "quantity", nullable = false)
  Integer quantity;

  @Column(name = "is_active", nullable = false)
  Boolean isActive;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  // 1-n với PaymentTransaction
  @OneToMany(mappedBy = "userPurchase")
  List<PaymentTransaction> paymentTransactions;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
