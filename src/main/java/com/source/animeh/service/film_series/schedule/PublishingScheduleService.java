package com.source.animeh.service.film_series.schedule;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.utils.MediaUtils.filterAndSortEpisodesByRole;

import com.source.animeh.dto.request.film_series.anime.schedule.CreateScheduleRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleFilterRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.dto.response.schedule.anime.PublishingScheduleResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.AnimeMapper;
import com.source.animeh.mapper.film_series.schedule.PublishingScheduleMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.schedule.PublishingScheduleRepository;
import com.source.animeh.specification.film_series.PublishingScheduleSpecification;
import com.source.animeh.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishingScheduleService {

  PublishingScheduleRepository publishingScheduleRepository;
  PublishingScheduleMapper publishingScheduleMapper;
  AnimeRepository animeRepository;
  AnimeMapper animeMapper;
  UserRepository userRepository;
  EpisodeRepository episodeRepository;
  EpisodeMapper episodeMapper;


  /**
   * Lấy danh sách schedule (có phân trang) tùy theo role: - ADMIN => lấy tất cả - MODERATOR => chỉ
   * lấy schedule của Anime do mình tạo
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public Page<PublishingScheduleResponse> getAllSchedules(int page, int size) {
    User user = SecurityUtils.getCurrentUser(userRepository);
    Pageable pageable = PageRequest.of(page, size);

    if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      Page<PublishingSchedule> all = publishingScheduleRepository.findAll(pageable);
      return all.map(ps -> publishingScheduleMapper.toScheduleResponse(ps, user));
    } else if ("ROLE_MODERATOR".equalsIgnoreCase(user.getRole().getName())) {
      // Lọc publishingSchedule bằng anime.submittedBy == currentUser
      Page<PublishingSchedule> pageResult = publishingScheduleRepository.findAll(
          (root, query, cb) -> {
            var joinAnime = root.join("anime");
            var joinUser = joinAnime.join("submittedBy");
            return cb.equal(joinUser.get("id"), user.getId());
          }, pageable);
      return pageResult.map(ps -> publishingScheduleMapper.toScheduleResponse(ps, user));
    }
    throw new AppException(ErrorCode.UNAUTHORIZED);
  }

  /**
   * Gắn 1 Anime khác vào schedule cũ
   */
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public PublishingScheduleResponse attachAnimeToSchedule(
      String scheduleId, String newAnimeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    PublishingSchedule ps = publishingScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    Anime newAnime = animeRepository.findById(newAnimeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      // Tìm schedule cũ của anime
      PublishingSchedule oldPs = newAnime.getPublishingSchedule();

      // Nếu oldPs != null và oldPs != ps => xóa schedule cũ
      if (oldPs != null && !oldPs.getId().equals(ps.getId())) {
        oldPs.setAnime(null);
      }

      // Gán anime vào ps
      newAnime.setPublishingSchedule(ps);
      ps.setAnime(newAnime);
    } else if ("ROLE_MODERATOR".equalsIgnoreCase(user.getRole().getName())) {
      // Check anime cũ
      if (!ps.getAnime().getSubmittedBy().getId().equals(user.getId())) {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
      // Check anime mới
      if (!newAnime.getSubmittedBy().getId().equals(user.getId())) {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
      // Tìm schedule cũ của anime
      PublishingSchedule oldPs = newAnime.getPublishingSchedule();

      // Nếu oldPs != null và oldPs != ps => xóa schedule cũ
      if (oldPs != null && !oldPs.getId().equals(ps.getId())) {
        oldPs.setAnime(null);
      }

      // Gán anime vào ps
      newAnime.setPublishingSchedule(ps);
      ps.setAnime(newAnime);
    } else {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    ps.setUpdatedAt(LocalDateTime.now());
    publishingScheduleRepository.save(ps);
    return publishingScheduleMapper.toScheduleResponse(ps, user);
  }

  // Lấy lịch bằng Anime Id
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public PublishingScheduleResponse getScheduleByAnimeId(
      String animeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    PublishingSchedule ps = publishingScheduleRepository
        .findByAnimeId(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    return publishingScheduleMapper.toScheduleResponse(ps, user);
  }

  // Lấy lịch
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public PublishingScheduleResponse getScheduleById(
      String scheduleId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    PublishingSchedule ps = publishingScheduleRepository
        .findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    return publishingScheduleMapper.toScheduleResponse(ps, user);
  }

  // Update lịch
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public PublishingScheduleResponse updateSchedule(
      String scheduleId,
      PublishingScheduleUpdateRequest request) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    PublishingSchedule ps = publishingScheduleRepository
        .findById(scheduleId)
        .orElseThrow(
            () -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND)
        );

    if (request.getModDeadline() != null) {
      if (request.getModDeadline().isAfter(ps.getScheduleDate())) {
        throw new AppException(ErrorCode.INVALID_DATE_UPDATE);
      }
    }

    if (request.getScheduleDate() != null
        && request.getModDeadline() != null) {
      if (request.getModDeadline().isAfter(request.getScheduleDate())) {
        throw new AppException(ErrorCode.INVALID_DATE_UPDATE);
      }
    }

    if (request.getAnimeId() != null) {
      Anime newAnime = animeRepository.findById(request.getAnimeId())
          .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

      // Kiểm tra role
      if ("ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
        ps.setAnime(newAnime);
      } else if ("ROLE_MODERATOR".equalsIgnoreCase(user.getRole().getName())) {
        // Kiểm tra anime cũ
        if (!ps.getAnime().getSubmittedBy().getId().equals(user.getId())) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        // Kiểm tra anime mới
        if (!newAnime.getSubmittedBy().getId().equals(user.getId())) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        ps.setAnime(newAnime);
      } else {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
    }

    // Partial update
    publishingScheduleMapper.updateSchedule(ps, request);

    // Tính lại modDeadline = scheduleDate - 2
    if (request.getScheduleDate() != null) {
      ps.setModDeadline(request.getScheduleDate().minusDays(2));
    }

    ps.setUpdatedAt(LocalDateTime.now());
    publishingScheduleRepository.save(ps);
    return publishingScheduleMapper.toScheduleResponse(ps, user);
  }

  /**
   * Cronjob chạy hằng ngày lúc 0h00 để kiểm tra deadline của tất cả Anime
   * <p>
   * - Nếu hiện tại > modDeadline => set statusDeadline = "QUA_HAN_MOD" - Nếu hiện tại >
   * scheduleDate => set statusDeadline = "QUA_HAN_ADMIN"
   * </p>
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  @Scheduled(cron = "0 0 0 * * ?")
  public void checkDeadline() {
    List<PublishingSchedule> all = publishingScheduleRepository.findAll();
    LocalDateTime now = LocalDateTime.now();
    for (PublishingSchedule ps : all) {
      // Nếu statusDeadline chưa phải QUA_HAN_ADMIN => ta check
      if (!"QUA_HAN_ADMIN".equals(ps.getStatusDeadline())
          && now.isAfter(ps.getScheduleDate())) {
        ps.setStatusDeadline("QUA_HAN_ADMIN");
      }
      // Nếu statusDeadline chưa phải QUA_HAN_MOD => ta check
      if (!"QUA_HAN_MOD".equals(ps.getStatusDeadline())
          && now.isAfter(ps.getModDeadline())) {
        ps.setStatusDeadline("QUA_HAN_MOD");
      }
      ps.setUpdatedAt(now);
      publishingScheduleRepository.save(ps);
    }
  }

  /**
   * Chỉ ADMIN được phép xóa schedule
   */
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteSchedule(String scheduleId) {
    User user = SecurityUtils.getCurrentUser(userRepository);
    if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    PublishingSchedule ps = publishingScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
    publishingScheduleRepository.delete(ps);
    log.info("Admin {} vừa xóa schedule {}", user.getUsername(), scheduleId);
  }

  /**
   * Filter theo request => trả về Page<PublishingScheduleResponse>
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public Page<PublishingScheduleResponse> filter(
      PublishingScheduleFilterRequest req,
      int page, int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Gọi spec
    Specification<PublishingSchedule> spec = PublishingScheduleSpecification.filter(req);

    Pageable pageable = PageRequest.of(page, size);

    // findAll
    Page<PublishingSchedule> pageResult = publishingScheduleRepository
        .findAll(spec, pageable);

    return pageResult.map(ps -> {
      // Tự build response
      PublishingScheduleResponse dto = new PublishingScheduleResponse();
      dto.setId(ps.getId());
      dto.setScheduleDate(ps.getScheduleDate());
      dto.setModDeadline(ps.getModDeadline());
      dto.setStatusDeadline(ps.getStatusDeadline());
      dto.setCreatedAt(ps.getCreatedAt());
      dto.setUpdatedAt(ps.getUpdatedAt());

      // Map anime => AnimeResponse
      if (ps.getAnime() != null) {
        AnimeResponse animeRes = animeMapper.toAnimeResponse(ps.getAnime(), user);

        // Tính tổng số tập
        int approvedCount = episodeRepository.countByAnimeIdAndStatus(
            ps.getAnime().getId(),
            EPISODE_REQUEST_APPROVED.getStatus()
        );
        animeRes.setTotalEpisodeCount(approvedCount);

        // Lấy danh sách episodes => set
        List<Episode> allEps = episodeRepository.findByAnimeId(ps.getAnime().getId());
        List<Episode> visibleEps = filterAndSortEpisodesByRole(allEps, user);

        // Map sang EpisodeInAnimeResponse
        List<EpisodeInAnimeResponse> epDtos = visibleEps.stream()
            .map(ep -> episodeMapper.toEpisodeInAnimeResponse(ep, user))
            .toList();
        animeRes.setEpisodes(epDtos);

        // Series => map => set animes
        if (ps.getAnime().getSeries() != null) {
          AnimeSeriesInAnimeResponse seriesDto = animeMapper.toAnimeSeriesInAnimeResponse(
              ps.getAnime().getSeries());
          animeRes.setSeries(seriesDto);
        }

        dto.setAnime(animeRes);
      }
      return dto;
    });
  }

  @PreAuthorize("hasRole('ADMIN')")
  public PublishingScheduleResponse createSchedule(CreateScheduleRequest request) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    PublishingSchedule ps = new PublishingSchedule();
    ps.setId(UUID.randomUUID().toString());
    if (request.getAnimeId() != null) {
      Anime anime = animeRepository
          .findById(request.getAnimeId())
          .orElse(null);
      ps.setAnime(anime);
    } else {
      ps.setAnime(null);
    }
    ps.setScheduleType("ANIME");
    ps.setDescription(request.getDescription());
    ps.setScheduleDate(
        request.getScheduleDate() != null ? request.getScheduleDate()
            : LocalDateTime.now().plusDays(3));
    ps.setModDeadline(ps.getScheduleDate().minusDays(2));
    ps.setStatusDeadline("ON_TIME");
    ps.setCreatedAt(LocalDateTime.now());

    publishingScheduleRepository.save(ps);

    return publishingScheduleMapper.toScheduleResponse(ps, user);
  }
}
