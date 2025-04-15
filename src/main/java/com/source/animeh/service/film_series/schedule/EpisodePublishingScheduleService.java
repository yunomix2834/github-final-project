package com.source.animeh.service.film_series.schedule;

import com.source.animeh.dto.request.film_series.anime.schedule.CreateEpisodeScheduleRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleFilterRequest;
import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.episode.AnimeInEpisodeResponse;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.schedule.episode.EpisodePublishingScheduleResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.schedule.EpisodePublishingScheduleMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.schedule.EpisodePublishingScheduleRepository;
import com.source.animeh.specification.film_series.EpisodePublishingScheduleSpecification;
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
public class EpisodePublishingScheduleService {

  EpisodePublishingScheduleRepository epsRepository;
  EpisodeRepository episodeRepository;
  EpisodePublishingScheduleMapper epsMapper;
  UserRepository userRepository;
  EpisodeMapper episodeMapper;

  /**
   * Lấy danh sách schedule (có phân trang) tùy theo role: - ADMIN => lấy tất cả - MODERATOR => chỉ
   * lấy những EpisodeSchedule mà Episode thuộc Anime do mình tạo
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public Page<EpisodePublishingScheduleResponse> getAllSchedules(int page, int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Pageable pageable = PageRequest.of(page, size);

    // Nếu ADMIN => trả về hết
    if (user.getRole().getName().equalsIgnoreCase("ROLE_ADMIN")) {
      Page<EpisodePublishingSchedule> all = epsRepository.findAll(pageable);
      return all.map(eps -> epsMapper.toEpisodeScheduleResponse(eps, user));
    }

    // Nếu MODERATOR => chỉ lấy schedule của Episode thuộc anime mà mình tạo
    else if (user.getRole().getName().equalsIgnoreCase("ROLE_MODERATOR")) {
      Page<EpisodePublishingSchedule> pageResult = epsRepository.findAll((root, query, cb) -> {
        var joinEp = root.join("episode");
        var joinAnime = joinEp.join("anime");
        var joinUser = joinAnime.join("submittedBy");
        return cb.equal(joinUser.get("id"), user.getId());
      }, pageable);

      return pageResult.map(eps -> epsMapper.toEpisodeScheduleResponse(eps, user));
    }

    // Nếu role khác (user thường) => KHÔNG cho phép
    throw new AppException(ErrorCode.UNAUTHORIZED);
  }

  // Lấy lịch bằng episodeId
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public EpisodePublishingScheduleResponse getEpisodeScheduleByEpisodeId(
      String episodeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    EpisodePublishingSchedule epsSchedule = epsRepository
        .findByEpisodeId(episodeId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    return epsMapper.toEpisodeScheduleResponse(epsSchedule, user);
  }

  // Lấy lịch
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public EpisodePublishingScheduleResponse getEpisodeScheduleById(
      String scheduleId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    EpisodePublishingSchedule epsSchedule = epsRepository
        .findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    return epsMapper.toEpisodeScheduleResponse(epsSchedule, user);
  }

  /**
   * Update schedule (có thể đổi sang episodeId khác, thay đổi scheduleDate, ...) - ADMIN => tự do -
   * MOD => chỉ được đổi if anime do mình tạo
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public EpisodePublishingScheduleResponse updateEpisodeSchedule(
      String scheduleId,
      EpisodePublishingScheduleUpdateRequest request) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    EpisodePublishingSchedule epSchedule = epsRepository
        .findById(scheduleId)
        .orElseThrow(
            () -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND)
        );

    if (request.getModDeadline() != null) {
      if (request.getModDeadline().isAfter(epSchedule.getScheduleDate())) {
        throw new AppException(ErrorCode.INVALID_DATE_UPDATE);
      }
    }

    if (request.getScheduleDate() != null
        && request.getModDeadline() != null) {
      if (request.getModDeadline().isAfter(request.getScheduleDate())) {
        throw new AppException(ErrorCode.INVALID_DATE_UPDATE);
      }
    }

    if (request.getEpisodeId() != null) {
      Episode newEp = episodeRepository
          .findById(request.getEpisodeId())
          .orElseThrow(() -> new AppException(ErrorCode.EPISODE_NOT_FOUND));

      // Kiểm tra role
      if (user.getRole().getName().equalsIgnoreCase("ROLE_ADMIN")) {
        epSchedule.setEpisode(newEp);
      } else if (user.getRole().getName().equalsIgnoreCase("ROLE_MODERATOR")) {
        // Chỉ cho phép nếu newEp.anime.submittedBy == user
        if (!newEp.getAnime().getSubmittedBy().getId().equals(user.getId())) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        // Kiểm tra episode cũ
        if (!epSchedule.getEpisode().getAnime().getSubmittedBy().getId().equals(user.getId())) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        epSchedule.setEpisode(newEp);
      } else {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
    }

    // Partial update
    epsMapper.updateEpisodeSchedule(epSchedule, request);

    // Tính lại modDeadline = scheduleDate - 2
    if (request.getScheduleDate() != null
        && request.getModDeadline() == null) {
      epSchedule.setModDeadline(request.getScheduleDate().minusDays(2));
    }

    epSchedule.setUpdatedAt(LocalDateTime.now());
    epsRepository.save(epSchedule);
    return epsMapper.toEpisodeScheduleResponse(epSchedule, user);
  }

  /**
   * Chỉ ADMIN được phép xóa (cancel) lịch đăng tập.
   */
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteEpisodeSchedule(String scheduleId) {

    User user = SecurityUtils.getCurrentUser(userRepository);
    if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    EpisodePublishingSchedule eps = epsRepository.findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    // Xoá
    epsRepository.delete(eps);
  }

  /**
   * Gắn 1 Episode mới vào 1 schedule đã tồn tại (attachEpisode). - ADMIN => gắn tự do - MOD => chỉ
   * gắn nếu Episode thuộc Anime do mình tạo
   */
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public EpisodePublishingScheduleResponse attachEpisodeToSchedule(String scheduleId,
      String newEpisodeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    EpisodePublishingSchedule eps = epsRepository.findById(scheduleId)
        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

    Episode newEp = episodeRepository.findById(newEpisodeId)
        .orElseThrow(() -> new AppException(ErrorCode.EPISODE_NOT_FOUND));

    EpisodePublishingSchedule oldEps = epsRepository.findByEpisodeId(newEpisodeId).orElse(null);

    if (oldEps != null && !oldEps.getId().equals(eps.getId())) {
      oldEps.setEpisode(null);
      oldEps.setUpdatedAt(LocalDateTime.now());
      epsRepository.save(oldEps);
      // **Flush để DB cập nhật ngay => tránh duplicate key**
      epsRepository.flush();
    }
    
    // Kiểm tra role
    if (user.getRole().getName().equalsIgnoreCase("ROLE_ADMIN")) {
      // Gắn luôn
      eps.setEpisode(newEp);
      newEp.setScheduledDate(eps.getScheduleDate());
      newEp.getAnime().setNextEpisodePublishingDate(eps.getScheduleDate());
    } else if (user.getRole().getName().equalsIgnoreCase("ROLE_MODERATOR")) {
      // Chỉ gắn nếu newEp.anime.submittedBy == user
      if (!newEp.getAnime().getSubmittedBy().getId().equals(user.getId())) {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
      // Ngoài ra, schedule cũ cũng phải thuộc anime do mình tạo
      if (!eps.getEpisode().getAnime().getSubmittedBy().getId().equals(user.getId())) {
        throw new AppException(ErrorCode.UNAUTHORIZED);
      }
      eps.setEpisode(newEp);
      newEp.setScheduledDate(eps.getScheduleDate());
      newEp.getAnime().setNextEpisodePublishingDate(eps.getScheduleDate());
    } else {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    eps.setUpdatedAt(LocalDateTime.now());
    epsRepository.save(eps);

    return epsMapper.toEpisodeScheduleResponse(eps, user);
  }

  /**
   * Cronjob chạy hằng ngày lúc 0h00 để kiểm tra deadline của tất cả Episode
   * <p>
   * - Nếu hiện tại > modDeadline => set statusDeadline = "QUA_HAN_MOD" - Nếu hiện tại >
   * scheduleDate => set statusDeadline = "QUA_HAN_ADMIN"
   * </p>
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  @Scheduled(cron = "0 0 0 * * ?")
  public void checkEpisodeDeadline() {
    List<EpisodePublishingSchedule> all = epsRepository.findAll();
    LocalDateTime now = LocalDateTime.now();
    for (EpisodePublishingSchedule eps : all) {
      // Nếu statusDeadline chưa phải QUA_HAN_ADMIN => ta check
      if (!"QUA_HAN_ADMIN".equals(eps.getStatusDeadline())
          && now.isAfter(eps.getScheduleDate())) {
        eps.setStatusDeadline("QUA_HAN_ADMIN");
      }
      // Nếu statusDeadline chưa phải QUA_HAN_MOD => ta check
      if (!"QUA_HAN_MOD".equals(eps.getStatusDeadline())
          && now.isAfter(eps.getModDeadline())) {
        eps.setStatusDeadline("QUA_HAN_MOD");
      }
      eps.setUpdatedAt(now);
      epsRepository.save(eps);
    }
  }

  /**
   * Filter theo request => trả về Page<EpisodePublishingScheduleResponse>
   */
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public Page<EpisodePublishingScheduleResponse> filter(
      EpisodePublishingScheduleFilterRequest req,
      int page, int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Gọi spec
    Specification<EpisodePublishingSchedule> spec = EpisodePublishingScheduleSpecification.filter(
        req);

    Pageable pageable = PageRequest.of(page, size);

    Page<EpisodePublishingSchedule> pageResult = epsRepository.findAll(spec, pageable);

    return pageResult.map(eps -> {
      EpisodePublishingScheduleResponse dto = new EpisodePublishingScheduleResponse();
      dto.setId(eps.getId());
      dto.setScheduleDate(eps.getScheduleDate());
      dto.setModDeadline(eps.getModDeadline());
      dto.setStatusDeadline(eps.getStatusDeadline());
      dto.setCreatedAt(eps.getCreatedAt());
      dto.setUpdatedAt(eps.getUpdatedAt());

      // Map episode => EpisodeResponse
      if (eps.getEpisode() != null) {
        EpisodeResponse epRes = episodeMapper.toEpisodeResponse(eps.getEpisode(), user);
        AnimeInEpisodeResponse animeRes = epRes.getAnime();

        epRes.setAnime(animeRes);
        dto.setEpisode(epRes);
      }
      return dto;
    });
  }

  @PreAuthorize("hasRole('ADMIN')")
  public EpisodePublishingScheduleResponse createEpisodeSchedule(
      CreateEpisodeScheduleRequest request) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    EpisodePublishingSchedule eps = new EpisodePublishingSchedule();
    eps.setId(UUID.randomUUID().toString());
    if (request.getEpisodeId() != null) {
      Episode ep = episodeRepository
          .findById(request.getEpisodeId())
          .orElse(null);
      eps.setEpisode(ep);
    } else {
      eps.setEpisode(null);
    }
    eps.setScheduleType("EPISODE");
    eps.setDescription(request.getDescription());
    eps.setScheduleDate(
        request.getScheduleDate() != null ? request.getScheduleDate()
            : LocalDateTime.now().plusDays(3));
    eps.setModDeadline(eps.getScheduleDate().minusDays(2));
    eps.setStatusDeadline("ON_TIME");
    eps.setCreatedAt(LocalDateTime.now());

    epsRepository.save(eps);

    return epsMapper.toEpisodeScheduleResponse(eps, user);
  }
}
