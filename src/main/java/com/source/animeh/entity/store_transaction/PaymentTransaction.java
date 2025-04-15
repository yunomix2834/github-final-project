package com.source.animeh.entity.store_transaction;

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
@Table(name = "[PAYMENT_TRANSACTION]")
public class PaymentTransaction {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  // user_purchase_id -> FK -> UserPurchase
  @ManyToOne
  @JoinColumn(name = "user_purchase_id", nullable = false)
  UserPurchase userPurchase;

  @Column(name = "transaction_type", length = 50, nullable = false)
  String transactionType;

  @Column(name = "amount", precision = 10, scale = 2, nullable = false)
  BigDecimal amount;

  @Column(name = "currency", length = 10, nullable = false)
  String currency;

  @Column(name = "transaction_date", nullable = false)
  LocalDateTime transactionDate;

  @Column(name = "status", length = 50, nullable = false)
  String status;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  String description;

  @Column(name = "transaction_code", length = 100)
  String transactionCode;

  @Column(name = "account_number", length = 50)
  String accountNumber;

  @Column(name = "counterpart_bank_code", length = 50)
  String counterpartBankCode;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
