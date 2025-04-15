package com.source.animeh.specification.film_series;

import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleFilterRequest;
import com.source.animeh.entity.account.User_;
import com.source.animeh.entity.film_series.Anime_;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule_;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification cho PublishingSchedule (lịch đăng Anime) Tham chiếu sang anime => so sánh
 * anime.submittedBy, anime.reviewedBy, ...
 */
public class PublishingScheduleSpecification {

  /**
   * Trả về 1 Specification tổng hợp từ PublishingScheduleFilterRequest
   */
  public static Specification<PublishingSchedule> filter(PublishingScheduleFilterRequest request) {
    return Specification
        .where(dateRange(request.getStartDate(), request.getEndDate()))
        .and(statusDeadlineEquals(request.getStatusDeadline()))
        .and(submittedByModerator(request.getSubmittedByModerator()))
        .and(reviewedByAdmin(request.getReviewedByAdmin()))
        .and(filterByAnimeStatus(request.getAnimeStatus()));
  }

  /**
   * Filter theo khoảng ngày (ngày "scheduleDate" nằm trong [start, end])
   */
  private static Specification<PublishingSchedule> dateRange(LocalDate start, LocalDate end) {
    return (root, query, cb) -> {
      if (start == null && end == null) {
        return null;
      }

      // map LocalDate -> LocalDateTime
      LocalDateTime startOfDay = (start != null) ? start.atStartOfDay() : null;
      LocalDateTime endOfDay = (end != null) ? end.atTime(23, 59, 59) : null;

      if (startOfDay != null && endOfDay != null) {
        // scheduleDate BETWEEN startOfDay AND endOfDay
        return cb.between(root.get(PublishingSchedule_.scheduleDate), startOfDay, endOfDay);
      } else if (startOfDay != null) {
        // scheduleDate >= startOfDay
        return cb.greaterThanOrEqualTo(root.get(PublishingSchedule_.scheduleDate), startOfDay);
      } else {
        // scheduleDate <= endOfDay
        return cb.lessThanOrEqualTo(root.get(PublishingSchedule_.scheduleDate), endOfDay);
      }
    };
  }

  /**
   * Filter theo statusDeadline (ví dụ: "ON_TIME", "QUA_HAN_MOD", "QUA_HAN_ADMIN",
   * "DONE_APPROVED_ON_TIME" ...)
   */
  private static Specification<PublishingSchedule> statusDeadlineEquals(String statusDeadline) {
    return (root, query, cb) -> {
      if (statusDeadline == null || statusDeadline.isBlank()) {
        return null;
      }
      return cb.equal(root.get(PublishingSchedule_.statusDeadline), statusDeadline);
    };
  }

  /**
   * Filter theo người đăng (Moderator) => join sang anime => submittedBy => username
   */
  private static Specification<PublishingSchedule> submittedByModerator(String moderatorUsername) {
    return (root, query, cb) -> {
      if (moderatorUsername == null || moderatorUsername.isBlank()) {
        return null;
      }
      // join sang anime => anime.submittedBy => user => username
      var joinAnime = root.join(PublishingSchedule_.anime);
      var joinUser = joinAnime.join(Anime_.submittedBy);
      return cb.equal(joinUser.get(User_.username), moderatorUsername);
    };
  }

  /**
   * Filter theo người duyệt (Admin) => join sang anime => reviewedBy => username
   */
  private static Specification<PublishingSchedule> reviewedByAdmin(String adminUsername) {
    return (root, query, cb) -> {
      if (adminUsername == null || adminUsername.isBlank()) {
        return null;
      }
      // join sang anime => anime.reviewedBy => user => username
      var joinAnime = root.join(PublishingSchedule_.anime);
      var joinUser = joinAnime.join(Anime_.reviewedBy);
      return cb.equal(joinUser.get(User_.username), adminUsername);
    };
  }

  private static Specification<PublishingSchedule> filterByAnimeStatus(String animeStatus) {
    return (root, query, cb) -> {
      if (animeStatus == null || animeStatus.isBlank()) {
        return null;
      }
      var joinAnime = root.join(PublishingSchedule_.anime, JoinType.LEFT);
      return cb.equal(joinAnime.get(Anime_.statusSubmitted), animeStatus);
    };
  }
}
