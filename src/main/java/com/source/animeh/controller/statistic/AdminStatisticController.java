package com.source.animeh.controller.statistic;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.response.statistic.StatisticResponse;
import com.source.animeh.service.statistic.AdminStatisticService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý thống kê dành cho Admin.
 * <p>
 * Controller này cho phép lấy dữ liệu thống kê của hệ thống. Các endpoint và thông báo được định
 * nghĩa dưới dạng hằng số để dễ bảo trì và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AdminStatisticController.BASE_ENDPOINT)
public class AdminStatisticController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/admin/statistic";
  // --- Thông báo phản hồi ---
  public static final String MSG_STATISTICS_SUCCESS = "Statistics success!";
  AdminStatisticService adminStatisticService;

  /**
   * Lấy dữ liệu thống kê của hệ thống.
   *
   * @return ApiResponse chứa thông tin thống kê
   */
  @GetMapping("/animes")
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<StatisticResponse> getStatisticAnimes() {
    StatisticResponse response = adminStatisticService.getStatisticAnimes();
    return ApiResponse.<StatisticResponse>builder()
        .message(MSG_STATISTICS_SUCCESS)
        .result(response)
        .build();
  }
}
