package com.source.animeh.entity.account;

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
@Table(name = "[USER]")
public class User {

  @Id
  @Column(name = "id", updatable = false, length = 36, nullable = false)
  String id;

  @Column(name = "user_name", length = 100, nullable = false, unique = true)
  String username;

  @Column(name = "password", length = 256, nullable = false)
  String password;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  String email;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  Role role;

  @Column(name = "display_name", length = 100)
  String displayName;

  @Column(name = "full_name", length = 100)
  String fullName;

  @Column(name = "avatar_url", length = 255)
  String avatarUrl;

  @Column(name = "background_url", length = 255)
  String backgroundUrl;

  @Column(name = "gender")
  Boolean gender;

  @Column(name = "bio", columnDefinition = "NVARCHAR(MAX)")
  String bio;

  @Column(name = "cccd")
  String cccd;

  @Column(name = "phone_number", columnDefinition = "VARCHAR(20)")
  String phoneNumber;

  @Column(name = "is_active", nullable = false)
  Boolean isActive;

  @Column(name = "last_login")
  LocalDateTime lastLogin;

  @Column(name = "gp_value", precision = 12, scale = 3)
  BigDecimal gpValue;

  @Column(name = "created_at", nullable = false)
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "user")
//    List<ModeratorRequest> moderatorRequests;
//
//    @OneToMany(mappedBy = "admin")
//    List<ModeratorRequest> moderatorRequestsAsAdmin;
//
//    @OneToMany(mappedBy = "submittedBy")
//    List<Anime> animesSubmitted;
//
//    @OneToMany(mappedBy = "reviewedBy")
//    List<Anime> animesReviewed;
//
//    @OneToMany(mappedBy = "submittedBy")
//    List<Episode> episodesSubmitted;
//
//    @OneToMany(mappedBy = "reviewedBy")
//    List<Episode> episodesReviewed;
//
//    @OneToMany(mappedBy = "user")
//    List<Comment> comments;
//
//    @OneToMany(mappedBy = "user")
//    List<Rating> ratings;
//
//    @OneToMany(mappedBy = "user")
//    List<Watchlist> watchlists;
//
//    @OneToMany(mappedBy = "user")
//    List<ViewingHistory> viewingHistories;
//
//    @OneToMany(mappedBy = "actionBy")
//    List<AnimeModerationHistory> animeModerationHistories;
//
//    @OneToMany(mappedBy = "actionBy")
//    List<EpisodeModerationHistory> episodeModerationHistories;
//
//    @OneToMany(mappedBy = "sender")
//    List<Notification> notificationsSent;
//
//    @OneToMany(mappedBy = "receiver")
//    List<Notification> notificationsReceived;
//
//    @OneToMany(mappedBy = "user")
//    List<UserPurchase> userPurchases;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

}
