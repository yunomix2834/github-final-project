package com.source.animeh.controller.film_series.schedule;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.film_series.anime.schedule.CreateEpisodeScheduleRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleFilterRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.schedule.episode.EpisodePublishingScheduleResponse;
import com.source.animeh.service.film_series.schedule.EpisodePublishingScheduleService;
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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public class EpisodePublishingScheduleController {

  EpisodePublishingScheduleService episodePublishingScheduleService;

  @GetMapping("/schedule-episode/by-episode/{episodeId}")
  ApiResponse<EpisodePublishingScheduleResponse> getEpisodeScheduleByEpisodeId(
      @PathVariable String episodeId) {
    EpisodePublishingScheduleResponse response = episodePublishingScheduleService
        .getEpisodeScheduleByEpisodeId(
            episodeId);
    return ApiResponse.<EpisodePublishingScheduleResponse>builder()
        .message("Get Schedule successfully!")
        .result(response)
        .build();
  }

  @GetMapping("/schedule-episode/{scheduleId}")
  ApiResponse<EpisodePublishingScheduleResponse> getEpisodeScheduleById(
      @PathVariable String scheduleId) {
    EpisodePublishingScheduleResponse response = episodePublishingScheduleService
        .getEpisodeScheduleById(
            scheduleId);
    return ApiResponse.<EpisodePublishingScheduleResponse>builder()
        .message("Get Schedule successfully!")
        .result(response)
        .build();
  }

  @PatchMapping("/schedule-episode/{scheduleId}")
  ApiResponse<EpisodePublishingScheduleResponse> updateEpisodeSchedule(
      @PathVariable String scheduleId,
      @RequestBody EpisodePublishingScheduleUpdateRequest request) {
    EpisodePublishingScheduleResponse response = episodePublishingScheduleService
        .updateEpisodeSchedule(
            scheduleId,
            request);
    return ApiResponse.<EpisodePublishingScheduleResponse>builder()
        .message("Update schedule success!")
        .result(response)
        .build();
  }

  // Filter Episode schedule
  @PostMapping("/schedule-episodes/filter")
  public ApiResponse<Page<EpisodePublishingScheduleResponse>> filterEpisodeSchedule(
      @RequestBody EpisodePublishingScheduleFilterRequest filterRequest,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<Page<EpisodePublishingScheduleResponse>>builder()
        .message("Filter Episode Schedule successfully!")
        .result(episodePublishingScheduleService.filter(filterRequest, page, size))
        .build();
  }

  // Lấy tất cả schedule (theo role)
  @GetMapping("/schedule-episodes")
  public ApiResponse<Page<EpisodePublishingScheduleResponse>> getAllEpisodeSchedules(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Page<EpisodePublishingScheduleResponse> result = episodePublishingScheduleService.getAllSchedules(
        page,
        size);
    return ApiResponse.<Page<EpisodePublishingScheduleResponse>>builder()
        .message("Get all episode schedules successfully!")
        .result(result)
        .build();
  }

  @PostMapping("/schedule-episode")
  public ApiResponse<EpisodePublishingScheduleResponse> createEpisodeSchedule(
      @RequestBody CreateEpisodeScheduleRequest request) {
    EpisodePublishingScheduleResponse result = episodePublishingScheduleService.createEpisodeSchedule(
        request);
    return ApiResponse.<EpisodePublishingScheduleResponse>builder()
        .message("Create episode schedule successfully!")
        .result(result)
        .build();
  }

  // Đính kèm (attach) 1 episodeId mới vào schedule
  @PatchMapping("/schedule-episode/{scheduleId}/attach-episode")
  public ApiResponse<EpisodePublishingScheduleResponse> attachEpisode(
      @PathVariable String scheduleId,
      @RequestParam String newEpisodeId
  ) {
    EpisodePublishingScheduleResponse res = episodePublishingScheduleService.attachEpisodeToSchedule(
        scheduleId,
        newEpisodeId);
    return ApiResponse.<EpisodePublishingScheduleResponse>builder()
        .message("Attach new episode success!")
        .result(res)
        .build();
  }

  // Xoá schedule (chỉ Admin)
  @DeleteMapping("/schedule-episode/{scheduleId}")
  public ApiResponse<Void> deleteEpisodeSchedule(@PathVariable String scheduleId) {
    episodePublishingScheduleService.deleteEpisodeSchedule(scheduleId);
    return ApiResponse.<Void>builder()
        .message("Delete episode schedule success!")
        .build();
  }
}
