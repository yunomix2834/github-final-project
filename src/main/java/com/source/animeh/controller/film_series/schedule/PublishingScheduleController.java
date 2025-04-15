package com.source.animeh.controller.film_series.schedule;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.film_series.anime.schedule.CreateScheduleRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleFilterRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.schedule.anime.PublishingScheduleResponse;
import com.source.animeh.service.film_series.schedule.PublishingScheduleService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý lịch phát hành của Anime.
 * <p>
 * Các thao tác bao gồm: lấy lịch phát hành và cập nhật lịch phát hành. Các endpoint và thông báo
 * được định nghĩa dưới dạng hằng số để dễ bảo trì.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public class PublishingScheduleController {

  // --- Base Endpoint ---
  // --- Endpoint cho schedule ---
  public static final String ENDPOINT_GET_SCHEDULE = "/schedule/by-anime/{animeId}";
  public static final String ENDPOINT_UPDATE_SCHEDULE = "/schedule/{scheduleId}";
  // --- Thông báo phản hồi ---
  public static final String MSG_GET_SCHEDULE_SUCCESS = "Get Schedule successfully!";
  public static final String MSG_UPDATE_SCHEDULE_SUCCESS = "Update schedule success!";
  PublishingScheduleService publishingScheduleService;

  /**
   * Lấy lịch phát hành của Anime.
   *
   * @param animeId mã định danh của Anime
   * @return ApiResponse chứa thông tin lịch phát hành của Anime
   */
  @GetMapping(ENDPOINT_GET_SCHEDULE)
  ApiResponse<PublishingScheduleResponse> getScheduleByAnimeId(@PathVariable String animeId) {
    PublishingScheduleResponse response = publishingScheduleService.getScheduleByAnimeId(animeId);
    return ApiResponse.<PublishingScheduleResponse>builder()
        .message(MSG_GET_SCHEDULE_SUCCESS)
        .result(response)
        .build();
  }

  @GetMapping("/schedule/{scheduleId}")
  ApiResponse<PublishingScheduleResponse> getScheduleById(@PathVariable String scheduleId) {
    PublishingScheduleResponse response = publishingScheduleService.getScheduleById(
        scheduleId);
    return ApiResponse.<PublishingScheduleResponse>builder()
        .message(MSG_GET_SCHEDULE_SUCCESS)
        .result(response)
        .build();
  }

  /**
   * Cập nhật lịch phát hành của Anime.
   *
   * @param scheduleId mã định danh của Anime
   * @param request    yêu cầu cập nhật lịch phát hành
   * @return ApiResponse chứa thông tin lịch phát hành mới sau khi cập nhật
   */
  @PatchMapping(ENDPOINT_UPDATE_SCHEDULE)
  ApiResponse<PublishingScheduleResponse> updateSchedule(@PathVariable String scheduleId,
      @RequestBody PublishingScheduleUpdateRequest request) {
    PublishingScheduleResponse response = publishingScheduleService.updateSchedule(scheduleId,
        request);
    return ApiResponse.<PublishingScheduleResponse>builder()
        .message(MSG_UPDATE_SCHEDULE_SUCCESS)
        .result(response)
        .build();
  }

  // Filter Anime schedule
  @PostMapping("/schedules/filter")
  public ApiResponse<Page<PublishingScheduleResponse>> filterAnimeSchedule(
      @RequestBody PublishingScheduleFilterRequest filterRequest,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<Page<PublishingScheduleResponse>>builder()
        .message("Filter Schedule successfully!")
        .result(publishingScheduleService.filter(filterRequest, page, size))
        .build();
  }


  @GetMapping("/schedules")
  public ApiResponse<Page<PublishingScheduleResponse>> getAllSchedules(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<PublishingScheduleResponse> result = publishingScheduleService.getAllSchedules(page, size);
    return ApiResponse.<Page<PublishingScheduleResponse>>builder()
        .message("Get all anime schedules successfully!")
        .result(result)
        .build();
  }

  @PostMapping("/schedule")
  public ApiResponse<PublishingScheduleResponse> createSchedule(
      @RequestBody CreateScheduleRequest request) {
    PublishingScheduleResponse result = publishingScheduleService.createSchedule(request);
    return ApiResponse.<PublishingScheduleResponse>builder()
        .message("Create anime schedule successfully!")
        .result(result)
        .build();
  }

  @PatchMapping("/schedule/{scheduleId}/attach-anime")
  public ApiResponse<PublishingScheduleResponse> attachAnime(
      @PathVariable String scheduleId,
      @RequestParam String newAnimeId
  ) {
    PublishingScheduleResponse res = publishingScheduleService.attachAnimeToSchedule(scheduleId,
        newAnimeId);
    return ApiResponse.<PublishingScheduleResponse>builder()
        .message("Attach anime success!")
        .result(res)
        .build();
  }

  @DeleteMapping("/schedule/{scheduleId}")
  public ApiResponse<Void> deleteSchedule(@PathVariable String scheduleId) {
    publishingScheduleService.deleteSchedule(scheduleId);
    return ApiResponse.<Void>builder()
        .message("Delete anime schedule success!")
        .build();
  }
}
