package com.source.animeh.specification.film_series;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_REJECTED;

import com.source.animeh.dto.request.filter.EpisodeFilterRequest;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EpisodeSpecification {

  // Lọc nâng cao
  public static Specification<Episode> advancedFilter(
      EpisodeFilterRequest req) {
    return (root, query, cb) -> {
      Predicate p = cb.conjunction();

      // Lọc theo animeId
      if (req.getAnimeId() != null && !req.getAnimeId().isBlank()) {
        p = cb.and(
            p,
            cb.equal(root.get("anime").get("id"), req.getAnimeId())
        );
      }

      // Tìm kiếm theo title
      if (req.getTitle() != null && !req.getTitle().isBlank()) {
        if (Boolean.TRUE.equals(req.getPartialTitle())) {
          p = cb.and(
              p,
              cb.like(cb.lower(root.get("title")), "%" + req.getTitle().toLowerCase() + "%")
          );
        } else {
          p = cb.and(
              p,
              cb.equal(cb.lower(root.get("title")), req.getTitle().toLowerCase())
          );
        }
      }

      // Lọc theo status
      if (req.getStatusFilters() != null && !req.getStatusFilters().isEmpty()) {
        p = cb.and(
            p,
            root.get("status").in(req.getStatusFilters())
        );
      }

      // Lọc theo người submit
      if (req.getSubmittedByUsername() != null
          && !req.getSubmittedByUsername().isBlank()) {
        p = cb.and(
            p,
            cb.equal(
                cb.lower(root.get("submittedBy").get("username")),
                req.getSubmittedByUsername().toLowerCase()
            )
        );
      }

      // Lọc theo người review
      if (req.getReviewedByUsername() != null
          && !req.getReviewedByUsername().isBlank()) {
        p = cb.and(
            p,
            cb.equal(
                cb.lower(root.get("reviewedBy").get("username")),
                req.getReviewedByUsername().toLowerCase()
            )
        );
      }

      return p;
    };
  }

  // Lọc theo role
  public static Specification<Episode> filterByRole(User user) {
    return (root, query, cb) -> {
      if (user == null) {
        // Chỉ lấy APPROVED
        return cb.equal(root.get("status"),
            EPISODE_REQUEST_APPROVED.getStatus());
      }
      switch (user.getRole().getName().toUpperCase()) {
        case "ROLE_ADMIN":
          return cb.conjunction(); // xem hết
        case "ROLE_MODERATOR":
          // Episode approved => hiển thị
          Predicate isApproved = cb.equal(root.get("status"),
              EPISODE_REQUEST_APPROVED.getStatus());
          // Hoặc episode do chính user submit + (pending hoặc rejected)
          Predicate isMine = cb.equal(root.get("submittedBy").get("id"), user.getId());
          Predicate isPendingOrRejected = root.get("status").in(EPISODE_REQUEST_PENDING.getStatus(),
              EPISODE_REQUEST_REJECTED.getStatus()
          );
          Predicate isMinePendingOrRejected = cb.and(isMine, isPendingOrRejected);

          return cb.or(isApproved, isMinePendingOrRejected);
        default:
          // ROLE_USER
          return cb.equal(root.get("status"), "APPROVED");
      }
    };
  }

  // Gộp 2 spec
  public static Specification<Episode> buildSpecWithRole(EpisodeFilterRequest req, User user) {
    return Specification
        .where(filterByRole(user))
        .and(advancedFilter(req));
  }
}

