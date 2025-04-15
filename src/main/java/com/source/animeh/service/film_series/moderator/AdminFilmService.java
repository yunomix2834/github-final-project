package com.source.animeh.service.film_series.moderator;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_REJECTED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_REJECTED;
import static com.source.animeh.constant.PredefinedStatus.STATUS_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.STATUS_REJECTED;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.episode.EpisodeModerationHistory;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeModeratorHistory;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.film_series.moderator.AdminFilmServiceInterface;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeModerationHistoryRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeModeratorHistoryRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.schedule.EpisodePublishingScheduleRepository;
import com.source.animeh.repository.film_series.schedule.PublishingScheduleRepository;
import com.source.animeh.service.film_series.VideoLibraryService;
import com.source.animeh.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


// TODO Approve Episode
@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminFilmService implements AdminFilmServiceInterface {

  AnimeRepository animeRepository;
  EpisodeRepository episodeRepository;
  VideoLibraryService videoLibraryService;
  UserRepository userRepository;

  AnimeModeratorHistoryRepository animeModeratorHistoryRepository;
  EpisodeModerationHistoryRepository episodeModerationHistoryRepository;

  PublishingScheduleRepository psRepository;
  EpisodePublishingScheduleRepository epsRepository;

  /**
   * Admin duyệt Anime => Move folder unapproved -> approved => set status_submitted=APPROVED
   */
  @Transactional
  @Override
  public void approveAnime(
      String animeId) {
    User admin = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository
        .findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    if (FILM_REQUEST_APPROVED
        .getStatus()
        .equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    // Duyệt rồi thì chỉ tạo folder thôi
    String seriesId = anime.getSeries().getId();
    videoLibraryService.moveAnimeFolder(
        "unapproved", "approved",
        seriesId, animeId);

    // Set db
    anime.setStatusSubmitted(FILM_REQUEST_APPROVED.getStatus());
    anime.setReviewedBy(admin);
    anime.setReviewedAt(LocalDateTime.now());
    animeRepository.save(anime);

//        // Duyệt luôn Episode => set "APPROVED", move folder
//        List<Episode> eps = episodeRepository.findByAnimeId(animeId);
//        for (Episode ep : eps) {
//            if(EPISODE_REQUEST_PENDING
//                    .getStatus()
//                    .equalsIgnoreCase(ep.getStatus())){
//                ep.setStatus(EPISODE_REQUEST_REJECTED.getStatus());
//                ep.setReviewedBy(admin);
//                ep.setReviewedAt(LocalDateTime.now());
//                // videoUrl => /hls/approved/<seriesId>/<animeId>/<episodeId>/master.m3u8
//                String newUrl = "/hls/approved/" + sId + "/" + animeId + "/" + ep.getId() + "/master.m3u8";
//                ep.setVideoUrl(newUrl);
//
//                // Move folder unapproved -> approved
//                videoLibraryService.moveEpisodeFolder(
//                        "unapproved", "approved",
//                        sId, animeId, ep.getId()
//                );
//                episodeRepository.save(ep);
//            }
//        }

    // Cập nhật schedule => check “quá hạn admin”?
    psRepository
        .findByAnimeId(animeId)
        .ifPresent(ps -> {
          if (LocalDateTime.now().isAfter(ps.getScheduleDate())) {
            ps.setStatusDeadline("QUA_HAN_ADMIN");
          } else {
            ps.setStatusDeadline("DONE_APPROVED_ON_TIME");
          }
          ps.setUpdatedAt(LocalDateTime.now());
          psRepository.save(ps);
        });

    // Ghi log Anime
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction(STATUS_APPROVED.getStatus());
    history.setReason("Approved Anime");
    history.setActionBy(admin);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);
  }

  /**
   * Admin từ chối Anime => set status_submitted=REJECTED, reason
   */
  @Transactional
  @Override
  public void rejectAnime(
      String animeId,
      String reason) {

    User admin = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository
        .findById(animeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_NOT_FOUND)
        );

    if (FILM_REQUEST_APPROVED
        .getStatus()
        .equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    anime.setStatusSubmitted(FILM_REQUEST_REJECTED.getStatus());
    anime.setRejectedReason(reason);
    anime.setReviewedBy(admin);
    anime.setReviewedAt(LocalDateTime.now());
    animeRepository.save(anime);

    // Cập nhật schedule => check “quá hạn admin”?
    psRepository
        .findByAnimeId(animeId)
        .ifPresent(ps -> {
          if (LocalDateTime.now().isAfter(ps.getScheduleDate())) {
            ps.setStatusDeadline("QUA_HAN_ADMIN");
          } else {
            ps.setStatusDeadline("DONE_REJECTED_ON_TIME");
          }
          ps.setUpdatedAt(LocalDateTime.now());
          psRepository.save(ps);
        });

    // Ghi log Anime
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction(STATUS_REJECTED.getStatus());
    history.setReason("Rejected Anime");
    history.setActionBy(admin);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);
  }

  /**
   * Admin duyệt tập phim => move folder unapproved -> approved => set status=APPROVED
   */
  @Transactional
  @Override
  public void approveEpisode(
      String episodeId) {

    User admin = SecurityUtils.getCurrentUser(userRepository);

    Episode episode = episodeRepository
        .findById(episodeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EPISODE_NOT_FOUND)
        );

    // Kiểm tra Episode đang PENDING
    if (EPISODE_REQUEST_APPROVED
        .getStatus()
        .equalsIgnoreCase(episode.getStatus())
    ) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    // Move folder: unapproved/<seriesId>/<animeId>/<episodeId> => approved/<seriesId>/<animeId>/<episodeId>
    String seriesId = episode.getAnime().getSeries().getId();
    String animeId = episode.getAnime().getId();

    videoLibraryService.moveEpisodeFolder(
        "unapproved", "approved",
        seriesId, animeId, episodeId
    );

    // Cập nhật DB
    episode.setStatus(EPISODE_REQUEST_APPROVED.getStatus());
    episode.setReviewedBy(admin);
    episode.setReviewedAt(LocalDateTime.now());

    // Sửa videoUrl => /hls/approved/<seriesId>/<animeId>/<episodeId>/master.m3u8
    String newUrl = "/hls/approved/" + seriesId + "/" + animeId + "/" + episodeId + "/master.m3u8";
    episode.setVideoUrl(newUrl);

    episodeRepository.save(episode);

    if (episode.getAnime().getNextEpisodePublishingDate() != null) {
      LocalDateTime oldAnimeEpDate = episode.getAnime().getNextEpisodePublishingDate();
      episode.getAnime().setNextEpisodePublishingDate(oldAnimeEpDate.plusDays(7));
    }
    
    // Cập nhật schedule => check “quá hạn admin”?
    epsRepository
        .findByEpisodeId(episodeId)
        .ifPresent(eps -> {
          if (LocalDateTime.now().isAfter(eps.getScheduleDate())) {
            eps.setStatusDeadline("QUA_HAN_ADMIN");
          } else {
            eps.setStatusDeadline("DONE_APPROVED_ON_TIME");
          }
          eps.setUpdatedAt(LocalDateTime.now());
          epsRepository.save(eps);
        });

    // Ghi log Episode
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(episode.getId());
    history.setAction(STATUS_APPROVED.getStatus());
    history.setReason("Approved Episode");
    history.setActionBy(admin);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);

    log.info("Approved episode {}, moved to folder approved/<seriesId>/<animeId>/<episodeId>",
        episodeId);
  }

  /**
   * Admin từ chối Episode => set status_submitted=REJECTED, reason
   */
  @Transactional
  @Override
  public void rejectEpisode(
      String episodeId,
      String reason) {

    User admin = SecurityUtils.getCurrentUser(userRepository);

    Episode episode = episodeRepository
        .findById(episodeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EPISODE_NOT_FOUND)
        );

    if (EPISODE_REQUEST_APPROVED
        .getStatus()
        .equalsIgnoreCase(episode.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    if (EPISODE_REQUEST_REJECTED
        .getStatus()
        .equalsIgnoreCase(episode.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_REJECTED);
    }

    episode.setRejectedReason(reason);
    episode.setStatus(EPISODE_REQUEST_REJECTED.getStatus());
    episode.setReviewedBy(admin);
    episode.setReviewedAt(LocalDateTime.now());
    episodeRepository.save(episode);

    // Cập nhật schedule => check “quá hạn admin”?
    epsRepository
        .findByEpisodeId(episodeId)
        .ifPresent(eps -> {
          if (LocalDateTime.now().isAfter(eps.getScheduleDate())) {
            eps.setStatusDeadline("QUA_HAN_ADMIN");
          } else {
            eps.setStatusDeadline("DONE_REJECTED_ON_TIME");
          }
          eps.setUpdatedAt(LocalDateTime.now());
          epsRepository.save(eps);
        });

    // Ghi log Episode
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(episode.getId());
    history.setAction(STATUS_REJECTED.getStatus());
    history.setReason("Rejected Episode");
    history.setActionBy(admin);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);
  }
}
