package com.source.animeh.service.statistic;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service chạy cronjob để thống kê anime hằng ngày.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticCronService {

  AdminStatisticService adminStatisticService;

  /**
   * Cronjob chạy vào 0h00 mỗi ngày (0 0 0 * * ?) Có thể cấu hình cron trong application.yml =>
   * spring.task.scheduling.pool.size=5 app.cron.statistic=0 0 0 * * ?
   */

  @Scheduled(cron = "${app.cron.statistic:0 0 0 * * ?}")
  public void dailyStatisticAnimes() {
    try {
      // Gọi hàm getStatisticAnimes
      var stat = adminStatisticService.getStatisticAnimes();
      // Tuỳ ý: Lưu DB, hoặc log
      log.info("[CRON] Daily statistic => {}", stat);
    } catch (Exception e) {
      log.error("[CRON] Lỗi khi thống kê anime: {}", e.getMessage(), e);
    }
  }
}
