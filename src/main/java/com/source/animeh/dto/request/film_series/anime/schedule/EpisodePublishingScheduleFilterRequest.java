package com.source.animeh.dto.request.film_series.anime.schedule;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO để chứa các tiêu chí filter cho EpisodePublishingSchedule (lịch đăng Episode). Filter: - Theo
 * ngày / tuần / tháng => gợi ý: startDate, endDate - Theo trạng thái đăng tải: "ON_TIME",
 * "QUA_HAN_MOD", "QUA_HAN_ADMIN", "DONE_APPROVED_ON_TIME"... - Filter theo người đăng (Moderator)
 * => join sang episode.submittedBy.username - Filter theo người duyệt (Admin) => join sang
 * episode.reviewedBy.username
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpisodePublishingScheduleFilterRequest {

  LocalDate startDate;  // ngày bắt đầu
  LocalDate endDate;    // ngày kết thúc

  String statusDeadline; // "ON_TIME", "QUA_HAN_MOD", "QUA_HAN_ADMIN", ...

  String submittedByModerator; // episode.submittedBy.username
  String reviewedByAdmin;      // episode.reviewedBy.username

  String episodeStatus;
}
