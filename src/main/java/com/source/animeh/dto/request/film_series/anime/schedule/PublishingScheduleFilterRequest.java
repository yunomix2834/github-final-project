package com.source.animeh.dto.request.film_series.anime.schedule;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO để chứa các tiêu chí filter cho PublishingSchedule (lịch đăng Anime). Filter: - Theo ngày /
 * tuần / tháng => gợi ý: startDate, endDate - Theo trạng thái đăng tải: "ON_TIME", "QUA_HAN_MOD",
 * "QUA_HAN_ADMIN", "DONE_APPROVED_ON_TIME"... - Filter theo người đăng (Moderator) => ta join sang
 * anime.submittedBy.username - Filter theo người duyệt (Admin) => ta join sang
 * anime.reviewedBy.username
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublishingScheduleFilterRequest {

  LocalDate startDate;  // nếu muốn lọc theo ngày bắt đầu
  LocalDate endDate;    // nếu muốn lọc theo ngày kết thúc

  // Lọc theo statusDeadline (ví dụ "ON_TIME", "QUA_HAN_MOD", "QUA_HAN_ADMIN", ...)
  String statusDeadline;

  // Lọc theo username của Moderator (người submit Anime)
  String submittedByModerator;

  // Lọc theo username của Admin (người duyệt)
  String reviewedByAdmin;

  String animeStatus;
}
