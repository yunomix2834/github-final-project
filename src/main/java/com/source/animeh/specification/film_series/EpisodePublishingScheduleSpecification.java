package com.source.animeh.specification.film_series;

import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleFilterRequest;
import com.source.animeh.entity.account.User_;
import com.source.animeh.entity.episode.Episode_;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule_;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class EpisodePublishingScheduleSpecification {

  public static Specification<EpisodePublishingSchedule> filter(
      EpisodePublishingScheduleFilterRequest req) {
    return Specification
        .where(dateRange(req.getStartDate(), req.getEndDate()))
        .and(statusDeadlineEquals(req.getStatusDeadline()))
        .and(submittedByModerator(req.getSubmittedByModerator()))
        .and(reviewedByAdmin(req.getReviewedByAdmin()))
        .and(filterByEpisodeStatus(req.getEpisodeStatus()));
  }

  /**
   * Filter theo khoảng ngày => scheduleDate
   */
  private static Specification<EpisodePublishingSchedule> dateRange(LocalDate start,
      LocalDate end) {
    return (root, query, cb) -> {
      if (start == null && end == null) {
        return null;
      }
      LocalDateTime startOfDay = (start != null) ? start.atStartOfDay() : null;
      LocalDateTime endOfDay = (end != null) ? end.atTime(23, 59, 59) : null;

      if (startOfDay != null && endOfDay != null) {
        return cb.between(root.get(EpisodePublishingSchedule_.scheduleDate), startOfDay, endOfDay);
      } else if (startOfDay != null) {
        return cb.greaterThanOrEqualTo(root.get(EpisodePublishingSchedule_.scheduleDate),
            startOfDay);
      } else {
        return cb.lessThanOrEqualTo(root.get(EpisodePublishingSchedule_.scheduleDate), endOfDay);
      }
    };
  }

  /**
   * Filter theo statusDeadline
   */
  private static Specification<EpisodePublishingSchedule> statusDeadlineEquals(
      String statusDeadline) {
    return (root, query, cb) -> {
      if (statusDeadline == null || statusDeadline.isBlank()) {
        return null;
      }
      return cb.equal(root.get(EpisodePublishingSchedule_.statusDeadline), statusDeadline);
    };
  }

  /**
   * Filter theo người đăng (Moderator) => join sang episode => submittedBy => username
   */
  private static Specification<EpisodePublishingSchedule> submittedByModerator(
      String moderatorUsername) {
    return (root, query, cb) -> {
      if (moderatorUsername == null || moderatorUsername.isBlank()) {
        return null;
      }
      var joinEp = root.join(EpisodePublishingSchedule_.episode);
      var joinUser = joinEp.join(Episode_.submittedBy);
      return cb.equal(joinUser.get(User_.username), moderatorUsername);
    };
  }

  /**
   * Filter theo người duyệt (Admin) => join sang episode => reviewedBy => username
   */
  private static Specification<EpisodePublishingSchedule> reviewedByAdmin(String adminUsername) {
    return (root, query, cb) -> {
      if (adminUsername == null || adminUsername.isBlank()) {
        return null;
      }
      var joinEp = root.join(EpisodePublishingSchedule_.episode);
      var joinUser = joinEp.join(Episode_.reviewedBy);
      return cb.equal(joinUser.get(User_.username), adminUsername);
    };
  }

  private static Specification<EpisodePublishingSchedule> filterByEpisodeStatus(
      String episodeStatus) {
    return (root, query, cb) -> {
      if (episodeStatus == null || episodeStatus.isBlank()) {
        return null;
      }
      var joinEp = root.join(EpisodePublishingSchedule_.episode, JoinType.LEFT);
      return cb.equal(joinEp.get(Episode_.status), episodeStatus);
    };
  }
}
