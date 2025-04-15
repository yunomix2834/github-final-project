package com.source.animeh.service.film_series.moderator;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_REJECTED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_REJECTED;
import static com.source.animeh.constant.PredefinedStatus.STATUS_CREATE;
import static com.source.animeh.constant.PredefinedStatus.STATUS_DELETE;
import static com.source.animeh.constant.PredefinedStatus.STATUS_UPDATE;
import static com.source.animeh.constant.media.PredefinedType.COUNTRY_TYPE;
import static com.source.animeh.constant.media.PredefinedType.GENRE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.MOVIE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.RELEASE_YEAR_TYPE;
import static com.source.animeh.constant.media.PredefinedType.SCHEDULE_TYPE;
import static com.source.animeh.constant.media.PredefinedType.STATUS_TYPE;
import static com.source.animeh.constant.media.PredefinedType.STUDIO_TYPE;

import com.source.animeh.configuration.event.anime.AnimeEvent;
import com.source.animeh.configuration.event.anime.AnimeEventSubject;
import com.source.animeh.dto.request.film_series.anime.AnimeCreateRequest;
import com.source.animeh.dto.request.film_series.anime.AnimeUpdateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesCreateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesUpdateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeCreateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeUpdateRequest;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.episode.EpisodeModerationHistory;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeModeratorHistory;
import com.source.animeh.entity.film_series.AnimeSeries;
import com.source.animeh.entity.film_series.AnimeType;
import com.source.animeh.entity.film_series.Type;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.film_series.moderator.ModeratorFilmServiceInterface;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.AnimeMapper;
import com.source.animeh.mapper.film_series.AnimeSeriesMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeModerationHistoryRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeModeratorHistoryRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.AnimeSeriesRepository;
import com.source.animeh.repository.film_series.AnimeTypeRepository;
import com.source.animeh.repository.film_series.TypeRepository;
import com.source.animeh.repository.film_series.schedule.EpisodePublishingScheduleRepository;
import com.source.animeh.repository.film_series.schedule.PublishingScheduleRepository;
import com.source.animeh.service.file.MediaStorageService;
import com.source.animeh.service.film_series.VideoLibraryService;
import com.source.animeh.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
public class ModeratorFilmService implements ModeratorFilmServiceInterface {

  AnimeSeriesRepository animeSeriesRepository;
  AnimeRepository animeRepository;
  EpisodeRepository episodeRepository;
  AnimeTypeRepository animeTypeRepository;
  TypeRepository typeRepository;
  AnimeModeratorHistoryRepository animeModeratorHistoryRepository;
  EpisodeModerationHistoryRepository episodeModerationHistoryRepository;

  VideoLibraryService videoLibraryService;
  MediaStorageService mediaStorageService;

  UserRepository userRepository;

  AnimeSeriesMapper animeSeriesMapper;
  AnimeMapper animeMapper;
  EpisodeMapper episodeMapper;

  PublishingScheduleRepository psRepository;
  EpisodePublishingScheduleRepository epsRepository;

  AnimeEventSubject animeEventSubject;

  /**
   * Tạo Series mới (có thể kèm poster/banner).
   */
  @Override
  public AnimeSeriesResponse createSeries(AnimeSeriesCreateRequest animeSeriesCreateRequest)
      throws IOException {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Map sang entity
    AnimeSeries animeSeries = animeSeriesMapper.toAnimeSeries(animeSeriesCreateRequest);
    animeSeries.setId(UUID.randomUUID().toString());
    animeSeries.setCreatedAt(LocalDateTime.now());
    animeSeriesRepository.save(animeSeries);

    // Nếu có posterFile, lưu
    if (animeSeriesCreateRequest.getPosterFile() != null
        && !animeSeriesCreateRequest.getPosterFile().isEmpty()) {
      String paths = mediaStorageService.storeSeriesMedia(
          animeSeriesCreateRequest.getPosterFile(),
          animeSeries.getId(),
          "poster"
      );
      animeSeries.setPosterUrl(paths); // original
    }

    // Nếu có bannerFile, lưu
    if (animeSeriesCreateRequest.getBannerFile() != null
        && !animeSeriesCreateRequest.getBannerFile().isEmpty()) {
      String paths = mediaStorageService.storeSeriesMedia(
          animeSeriesCreateRequest.getBannerFile(),
          animeSeries.getId(),
          "banner"
      );
      animeSeries.setBannerUrl(paths);
    }
    animeSeriesRepository.save(animeSeries);

    // Tạo folder cứng cho series (nếu bạn muốn)
    // videoLibraryService.createSeriesFolder(animeSeries.getId());

    return animeSeriesMapper.toAnimeSeriesResponse(animeSeries, user);
  }

  /**
   * UPDATE Series (partial)
   */
  @Override
  public AnimeSeriesResponse updateSeries(String seriesId,
      AnimeSeriesUpdateRequest animeSeriesUpdateRequest) throws IOException {

    User user = SecurityUtils.getCurrentUser(userRepository);

    AnimeSeries animeSeries = animeSeriesRepository.findById(seriesId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND));

    // Partial update
    animeSeriesMapper.updateAnimeSeries(animeSeries, animeSeriesUpdateRequest);
    animeSeries.setUpdatedAt(LocalDateTime.now());

    // Nếu có posterFile => upload
    if (animeSeriesUpdateRequest.getPosterFile() != null
        && !animeSeriesUpdateRequest.getPosterFile().isEmpty()) {
      String paths = mediaStorageService.storeSeriesMedia(
          animeSeriesUpdateRequest.getPosterFile(), seriesId, "poster");
      animeSeries.setPosterUrl(paths);
    }
    // Nếu có bannerFile => upload
    if (animeSeriesUpdateRequest.getBannerFile() != null
        && !animeSeriesUpdateRequest.getBannerFile().isEmpty()) {
      String paths = mediaStorageService.storeSeriesMedia(
          animeSeriesUpdateRequest.getBannerFile(), seriesId, "banner");
      animeSeries.setBannerUrl(paths);
    }

    animeSeriesRepository.save(animeSeries);
    return animeSeriesMapper.toAnimeSeriesResponse(animeSeries, user);
  }

  /**
   * Xoá Series - Nếu Series đang có Anime => chặn xóa,
   */
  @Override
  @Transactional
  public void deleteSeries(String seriesId) {
    AnimeSeries series = animeSeriesRepository.findById(seriesId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND));

    // Kiểm tra series có anime không
    if (series.getAnimes() != null && !series.getAnimes().isEmpty()) {
      throw new AppException(ErrorCode.CANNOT_DELETE_FILES_IN_SERIES);
    }

    // Xoá DB
    animeSeriesRepository.delete(series);
  }

  /**
   * Tạo Anime => status_submitted = "PENDING" => folder: unapproved/<seriesId>/<animeId> => Nếu
   * seriesId=null => tự tạo series
   */
  // TODO có thể bổ sung thêm vài trường thông tin cho bộ anime
  @Override
  @Transactional
  public AnimeResponse createAnimePending(AnimeCreateRequest animeCreateRequest)
      throws IOException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    // Kiểm tra seriesId=null => tự tạo Series
    AnimeSeries animeSeries;
    if (animeCreateRequest.getSeriesId() == null) {
      // Tạo series
      AnimeSeriesCreateRequest animeSeriesCreateRequest = new AnimeSeriesCreateRequest();
      animeSeriesCreateRequest.setTitle(animeCreateRequest.getTitle());
      animeSeriesCreateRequest.setDescription(animeCreateRequest.getDescription());
      AnimeSeriesResponse newSeriesResponse = this.createSeries(animeSeriesCreateRequest);

      animeSeries = animeSeriesRepository.findById(newSeriesResponse.getId())
          .orElseThrow(() -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND));
    } else {
      animeSeries = animeSeriesRepository.findById(animeCreateRequest.getSeriesId())
          .orElseThrow(() -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND));
    }

    // Map sang anime
    Anime anime = animeMapper.toAnime(animeCreateRequest);
    anime.setId(UUID.randomUUID().toString());
    anime.setSeries(animeSeries);
    anime.setStatusSubmitted(FILM_REQUEST_PENDING.getStatus());
    anime.setSubmittedAt(LocalDateTime.now());
    anime.setSubmittedBy(moderator);
    anime.setCreatedAt(LocalDateTime.now());
    anime.setAverageRating(BigDecimal.ZERO);
    anime.setSeriesOrder("Phần 1");

    // releaseYear + trailerUrl
    if (animeCreateRequest.getReleaseYear() != null) {
      anime.setReleaseYear(animeCreateRequest.getReleaseYear());
    }
    if (animeCreateRequest.getLinkTrailer() != null) {
      anime.setTrailerUrl(animeCreateRequest.getLinkTrailer());
    }

    animeRepository.save(anime);

    // Upload poster/banner nếu có
    if (animeCreateRequest.getPosterFile() != null && !animeCreateRequest.getPosterFile()
        .isEmpty()) {
      String posterPaths = mediaStorageService.storeAnimeMedia(
          animeCreateRequest.getPosterFile(),
          anime.getId(),
          "poster"
      );
      anime.setPosterUrl(posterPaths);
    }

    if (animeCreateRequest.getBannerFile() != null && !animeCreateRequest.getBannerFile()
        .isEmpty()) {
      String bannerPaths = mediaStorageService.storeAnimeMedia(
          animeCreateRequest.getBannerFile(),
          anime.getId(),
          "banner"
      );
      anime.setBannerUrl(bannerPaths);
    }

    animeRepository.save(anime);

    // Gán typeIds nếu có
    if (animeCreateRequest.getTypeIds() != null && !animeCreateRequest.getTypeIds().isEmpty()) {
      for (String typeId : animeCreateRequest.getTypeIds()) {
        addTypeToAnime(anime, typeId);
      }
    }

    setDefaultTypesWhenCreate(anime, animeCreateRequest);

    syncAnimeStatusByEpisodeCount(anime);

    // Tạo folder unapproved/<seriesId>/<animeId>
    videoLibraryService.createAnimeFolder("unapproved", animeSeries.getId(), anime.getId());

    // Tạo schedule
    if (animeCreateRequest.getScheduleDate() != null) {
      LocalDateTime scheduleDateTime = animeCreateRequest
          .getScheduleDate()
          .atTime(0, 0); // => 0h

      PublishingSchedule ps = new PublishingSchedule();
      ps.setId(UUID.randomUUID().toString());
      ps.setAnime(anime);
      ps.setScheduleType("ANIME");
      ps.setDescription("Create Anime: " + anime.getTitle());
      ps.setScheduleDate(scheduleDateTime); // limit cho admin
      ps.setModDeadline(scheduleDateTime.minusDays(2)); // limit cho mod

      // Check ngay khi tạo => mod có trễ không?
      if (LocalDateTime.now().isAfter(ps.getModDeadline())) {
        ps.setStatusDeadline("QUA_HAN_MOD");
      } else {
        ps.setStatusDeadline("ON_TIME");
      }
      ps.setCreatedAt(LocalDateTime.now());

      psRepository.save(ps);
    }

    // Ghi log vào bảng ANIME_MODERATOR_HISTORY
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction(STATUS_CREATE.getStatus());
    history.setReason("Create Anime");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);

    AnimeResponse dto = animeMapper.toAnimeResponse(anime, moderator);
    dto.setEpisodes(Collections.emptyList());
    if (anime.getSeries() != null) {
      dto.setSeries(animeMapper.toAnimeSeriesInAnimeResponse(anime.getSeries()));
    } else {
      dto.setSeries(null);
    }

    AnimeEvent event = new AnimeEvent("CREATE ANIME", anime, moderator);
    animeEventSubject.notifyAllObservers(event);

    return dto;
  }

  /**
   * UPDATE Anime (partial), nếu PENDING hoặc APPROVED
   */
  @Transactional
  @Override
  public void updateAnime(
      String animeId,
      AnimeUpdateRequest animeUpdateRequest)
      throws IOException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    // Nếu anime = rejected => chuyển sang pending trước
    if (FILM_REQUEST_REJECTED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      anime.setStatusSubmitted(FILM_REQUEST_PENDING.getStatus());
      anime.setRejectedReason(null);
      anime.setReviewedBy(null);
      anime.setReviewedAt(null);
      // Re-submit => set submittedBy = moderator
      anime.setSubmittedBy(moderator);
      anime.setSubmittedAt(LocalDateTime.now());
    }

    // Nếu anime = approved => ko cho update
    if (FILM_REQUEST_APPROVED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    animeMapper.updateAnime(anime, animeUpdateRequest);

    // Upload poster/banner
    if (animeUpdateRequest.getPosterFile() != null && !animeUpdateRequest.getPosterFile()
        .isEmpty()) {
      String posterPaths = mediaStorageService.storeAnimeMedia(animeUpdateRequest.getPosterFile(),
          animeId, "poster");
      anime.setPosterUrl(posterPaths);
    }

    if (animeUpdateRequest.getBannerFile() != null && !animeUpdateRequest.getBannerFile()
        .isEmpty()) {
      String bannerPaths = mediaStorageService.storeAnimeMedia(animeUpdateRequest.getBannerFile(),
          animeId, "banner");
      anime.setBannerUrl(bannerPaths);
    }

    // Xóa & Thêm lại các pivot type theo field typeIds
    if (animeUpdateRequest.getTypeIds() != null) {

      List<AnimeType> oldPivots = animeTypeRepository
          .findAll()
          .stream()
          .filter(at -> at.getAnime().getId().equals(animeId))
          .toList();
      animeTypeRepository.deleteAll(oldPivots);
      anime.getAnimeTypes().clear();

      for (String typeId : animeUpdateRequest.getTypeIds()) {
        addTypeToAnime(anime, typeId);
      }
    }

    updateTypesForAnime(anime, animeUpdateRequest);

    syncAnimeStatusByEpisodeCount(anime);

    anime.setUpdatedAt(LocalDateTime.now());
    animeRepository.save(anime);

    // Ghi trạng thái schedule vào trong bảng Schedule
    psRepository
        .findByAnimeId(animeId)
        .ifPresent(ps -> {
          // Mod đang sửa => check modDeadline
          if (LocalDateTime.now().isAfter(ps.getModDeadline())) {
            ps.setStatusDeadline("QUA_HAN_MOD");
            ps.setUpdatedAt(LocalDateTime.now());
            psRepository.save(ps);
          }
        });

    // Ghi log Anime
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction(STATUS_UPDATE.getStatus());
    history.setReason("Updated Anime info");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);
  }

  // TODO để thành biến
  @Transactional
  public void revertAnimeToPending(String animeId) {
    User currentUser = SecurityUtils.getCurrentUser(userRepository);

    // Lấy anime
    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    if (!FILM_REQUEST_APPROVED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      // Nếu không phải APPROVED => throw
      throw new AppException(ErrorCode.INVALID_STATUS);
    }

    // Cập nhật lại thành PENDING
    anime.setStatusSubmitted(FILM_REQUEST_PENDING.getStatus());
    anime.setRejectedReason(null);       // Xoá reason cũ
    anime.setReviewedBy(null);          // Xoá reviewer cũ
    anime.setReviewedAt(null);          // Xoá thời gian review cũ
    // "re-submit"
    anime.setSubmittedBy(currentUser);
    anime.setSubmittedAt(LocalDateTime.now());

    animeRepository.save(anime);

    // Ghi log
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction("revert to pending");
    history.setReason("Revert from rejected to pending");
    history.setActionBy(currentUser);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);
  }


  // Xoá Anime
  @Transactional
  @Override
  public void deleteAnime(String animeId) throws IOException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    if (FILM_REQUEST_APPROVED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    // Ghi log Anime
    AnimeModeratorHistory history = new AnimeModeratorHistory();
    history.setId(UUID.randomUUID().toString());
    history.setAnimeId(anime.getId());
    history.setAction(STATUS_DELETE.getStatus());
    history.setReason("Deleted Anime");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    animeModeratorHistoryRepository.save(history);

    // Xóa schedule
    psRepository.findByAnimeId(animeId).ifPresent(psRepository::delete);

    // Xoá db
    animeRepository.delete(anime);

    // Xoá folder unapproved/<seriesId>/<animeId>
    String sId = anime.getSeries().getId();
    videoLibraryService.deleteFolder("unapproved", sId, animeId);
  }

  /**
   * Tạo Episode => PENDING => unapproved/<seriesId>/<animeId>/<episodeId>
   */
  // TODO đường dẫn thành biến
  @Transactional
  @Override
  public EpisodeResponse createEpisode(
      EpisodeCreateRequest episodeCreateRequest)
      throws IOException, InterruptedException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository.findById(episodeCreateRequest.getAnimeId())
        .orElseThrow(() -> new RuntimeException("Anime not found"));

    if (FILM_REQUEST_PENDING.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_PENDING);
    }

    if (FILM_REQUEST_REJECTED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_REJECTED);
    }

    // Lấy tất cả episodes của anime
    List<Episode> existingEps = episodeRepository.findByAnimeId(anime.getId());

    // Kiểm tra xem có trùng epNumber nếu tập cũ = APPROVED
    boolean hasApprovedDuplicate = existingEps.stream()
        .anyMatch(ep -> ep.getEpisodeNumber().equals(episodeCreateRequest.getEpisodeNumber())
            && "approved".equalsIgnoreCase(ep.getStatus()));
    if (hasApprovedDuplicate) {
      // Đã tồn tại 1 Episode APPROVED cùng số tập -> Không cho đăng
      throw new AppException(ErrorCode.EPISODE_NUMBER_ALREADY_APPROVED);
    }

    // Kiểm tra "không được đăng tập mới nhỏ hơn số tập approved cao nhất"
    Integer maxApproved = existingEps.stream()
        .filter(ep -> "approved".equalsIgnoreCase(ep.getStatus()))
        .map(Episode::getEpisodeNumber)
        .max(Integer::compareTo)
        .orElse(0);
    if (episodeCreateRequest.getEpisodeNumber() < maxApproved) {
      throw new AppException(ErrorCode.EPISODE_NUMBER_INVALID);
    }

    Episode ep = episodeMapper.toEpisode(episodeCreateRequest);
    ep.setId(UUID.randomUUID().toString());
    ep.setAnime(anime);
    ep.setStatus(EPISODE_REQUEST_PENDING.getStatus());
    ep.setSubmittedBy(moderator);
    ep.setSubmittedAt(LocalDateTime.now());
    ep.setCreatedAt(LocalDateTime.now());
    ep.setVideoUrl("");

    episodeRepository.save(ep);

    // Gán scheduledDate
    if (episodeCreateRequest.getScheduledDate() != null) {
      ep.setScheduledDate(episodeCreateRequest.getScheduledDate());

      LocalDateTime scheduleDateTime = episodeCreateRequest.getScheduledDate();
      EpisodePublishingSchedule eps = new EpisodePublishingSchedule();
      eps.setId(UUID.randomUUID().toString());
      eps.setEpisode(ep);
      eps.setScheduleType("EPISODE");
      eps.setDescription("Create Episode: " + ep.getEpisodeNumber());
      eps.setScheduleDate(scheduleDateTime);
      eps.setModDeadline(scheduleDateTime.minusDays(2));

      // Check ngay khi tạo => mod có trễ không?
      if (LocalDateTime.now().isAfter(eps.getModDeadline())) {
        eps.setStatusDeadline("QUA_HAN_MOD");
      } else {
        eps.setStatusDeadline("ON_TIME");
      }
      eps.setCreatedAt(LocalDateTime.now());

      epsRepository.save(eps);
    } else {
      // Fallback
      ep.setScheduledDate(LocalDateTime.now().plusDays(3));

      LocalDateTime scheduleDateTime = episodeCreateRequest.getScheduledDate();
      EpisodePublishingSchedule eps = new EpisodePublishingSchedule();
      eps.setId(UUID.randomUUID().toString());
      eps.setEpisode(ep);
      eps.setScheduleType("EPISODE");
      eps.setDescription("Create Episode: " + ep.getEpisodeNumber());
      eps.setScheduleDate(scheduleDateTime);
      eps.setModDeadline(scheduleDateTime.minusDays(2));
      eps.setStatusDeadline("ON_TIME");
      eps.setCreatedAt(LocalDateTime.now());

      epsRepository.save(eps);
    }

    episodeRepository.save(ep);

    // Bắt buộc upload video => transcode
    String tmpPath =
        "F:/fileHoc/animeh/tmp_upload/" + episodeCreateRequest.getVideoFile().getOriginalFilename();
    File tmp = new File(tmpPath);
    episodeCreateRequest.getVideoFile().transferTo(tmp);

    String seriesId = anime.getSeries().getId();
    String animeId = anime.getId();
    // ffmpeg => unapproved/<seriesId>/<animeId>/<episodeId>
    videoLibraryService.transcodeEpisodeHls("unapproved", seriesId, animeId, ep.getId(), tmpPath);
    tmp.delete();

    // set videoUrl
    String url = "/hls/unapproved/" + seriesId + "/" + animeId + "/" + ep.getId() + "/master.m3u8";
    ep.setVideoUrl(url);
    episodeRepository.save(ep);

    // Ghi log Episode
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(ep.getId());
    history.setAction(STATUS_CREATE.getStatus());
    history.setReason("Created Episode");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);

    return episodeMapper.toEpisodeResponse(ep, moderator);
  }

  /**
   * Sửa Episode (nếu PENDING hoặc APPROVED, không sửa video)
   */
  @Transactional
  @Override
  public void updateEpisode(String episodeId,
      EpisodeUpdateRequest episodeUpdateRequest)
      throws IOException, InterruptedException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Episode ep = episodeRepository
        .findById(episodeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EPISODE_NOT_FOUND)
        );

    if (EPISODE_REQUEST_REJECTED.getStatus().equalsIgnoreCase(ep.getStatus())) {
      // Đổi sang PENDING
      ep.setStatus(EPISODE_REQUEST_PENDING.getStatus());
      ep.setRejectedReason(null);
      ep.setReviewedBy(null);
      ep.setReviewedAt(null);
      // Ai "re-submit" thì set lại
      ep.setSubmittedBy(moderator);
      ep.setSubmittedAt(LocalDateTime.now());
    }

    if (EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(ep.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    episodeMapper.updateEpisode(ep, episodeUpdateRequest);
    ep.setUpdatedAt(LocalDateTime.now());

    // Nếu có videoFile => ghi đè folder unapproved/<seriesId>/<animeId>/<episodeId>
    if (episodeUpdateRequest.getVideoFile() != null && !episodeUpdateRequest.getVideoFile()
        .isEmpty()) {
      // Xóa thư mục cũ, transcode mới
      String tmpPath = "F:/fileHoc/animeh/tmp_upload/"
          + episodeUpdateRequest.getVideoFile()
          .getOriginalFilename();
      File tmp = new File(tmpPath);
      episodeUpdateRequest.getVideoFile().transferTo(tmp);

      Anime anime = ep.getAnime();
      String seriesId = anime.getSeries().getId();
      String animeId = anime.getId();

      // Xoá toàn bộ folder cũ (unapproved/seriesId/animeId/episodeId)
      videoLibraryService.deleteFolder("unapproved", seriesId, animeId, ep.getId());

      // Tạo lại folder -> transcode
      videoLibraryService.transcodeEpisodeHls(
          "unapproved",
          seriesId,
          animeId,
          ep.getId(),
          tmpPath
      );
      tmp.delete();

      // Cập nhật videoUrl => /hls/unapproved/<seriesId>/<animeId>/<episodeId>/master.m3u8
      String newUrl =
          "/hls/unapproved/" + seriesId + "/" + animeId + "/" + ep.getId() + "/master.m3u8";
      ep.setVideoUrl(newUrl);
    }

    episodeRepository.save(ep);

    // Ghi trạng thái schedule vào trong bảng Schedule
    epsRepository
        .findByEpisodeId(ep.getId())
        .ifPresent(eps -> {
          // Mod đang sửa => check modDeadline
          if (LocalDateTime.now().isAfter(eps.getModDeadline())) {
            eps.setStatusDeadline("QUA_HAN_MOD");
            eps.setUpdatedAt(LocalDateTime.now());
            epsRepository.save(eps);
          }
        });

    // Ghi log Episode
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(ep.getId());
    history.setAction(STATUS_UPDATE.getStatus());
    history.setReason("Updated Episode info");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);
  }

  @Transactional
  public void revertEpisodeToPending(String episodeId) {
    User currentUser = SecurityUtils.getCurrentUser(userRepository);

    Episode episode = episodeRepository.findById(episodeId)
        .orElseThrow(() -> new AppException(ErrorCode.EPISODE_NOT_FOUND));

    if (!EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(episode.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS);
    }

    // Đổi sang PENDING
    episode.setStatus(EPISODE_REQUEST_PENDING.getStatus());
    episode.setRejectedReason(null);
    episode.setReviewedBy(null);
    episode.setReviewedAt(null);
    // Ai "re-submit" thì set lại
    episode.setSubmittedBy(currentUser);
    episode.setSubmittedAt(LocalDateTime.now());

    episodeRepository.save(episode);

    // Ghi log
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(episode.getId());
    history.setAction("revert to pending");
    history.setReason("Revert from rejected to pending");
    history.setActionBy(currentUser);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);
  }

  /**
   * Xoá Episode
   */
  // TODO đường dẫn sửa thành biến
  @Transactional
  @Override
  public void deleteEpisode(String episodeId) throws IOException {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Episode ep = episodeRepository.findById(episodeId)
        .orElseThrow(() -> new RuntimeException("Episode not found"));

    if (EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(ep.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_APPROVED);
    }

    // Ghi log Episode
    EpisodeModerationHistory history = new EpisodeModerationHistory();
    history.setId(UUID.randomUUID().toString());
    history.setEpisodeId(ep.getId());
    history.setAction(STATUS_DELETE.getStatus());
    history.setReason("Deleted Episode");
    history.setActionBy(moderator);
    history.setActionAt(LocalDateTime.now());
    history.setCreatedAt(LocalDateTime.now());
    episodeModerationHistoryRepository.save(history);

    // Xóa schedule
    epsRepository.findByEpisodeId(episodeId).ifPresent(epsRepository::delete);

    episodeRepository.delete(ep);

    // Xoá folder unapproved/<seriesId>/<animeId>/<episodeId>
    String sId = ep.getAnime().getSeries().getId();
    String aId = ep.getAnime().getId();
    videoLibraryService.deleteFolder("unapproved", sId, aId, ep.getId());
  }


  // Update Series Anime
  @Override
  @Transactional
  public AnimeResponse updateAnimeSeries(String animeId,
      String newSeriesId) {

    User moderator = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    AnimeSeries newSeries = animeSeriesRepository.findById(newSeriesId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND));

    // Xác định storeType = unapproved/approved tuỳ trạng thái
    String storeType;
    if (FILM_REQUEST_APPROVED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      storeType = "approved";
    } else {
      storeType = "unapproved";
    }

    String oldSeriesId = anime.getSeries().getId();

    // Đổi anime sang newSeries
    anime.setSeries(newSeries);
    // TODO Sửa tuỳ ý cập nhật "seriesOrder", v.v...
    anime.setSeriesOrder("Phần 2");
    anime.setUpdatedAt(LocalDateTime.now());
    animeRepository.save(anime);

    // Rename folder
    // Từ storeType/oldSeriesId/animeId => storeType/newSeriesId/animeId
    videoLibraryService.moveAnimeFolder(storeType, storeType, oldSeriesId, animeId);

    return animeMapper.toAnimeResponse(anime, moderator);
  }

  /**
   * Gán các type mặc định: - Loại phim = "Phim bộ" - Tình trạng = "Sắp chiếu" - Thể loại   =
   * "Anime" - Năm phát hành: = request.releaseYear (nếu != null) - Quốc gia: user truyền
   * request.getCountryId() (nếu có) - Studio: ... - Lịch chiếu: user có thể truyền 1 field
   * scheduleDay = "Thứ hai"...
   */
  // TODO Refactor
  // TODO lọc chi tiết hơn các lỗi về các type khác nhau
  void setDefaultTypesWhenCreate(
      Anime anime,
      AnimeCreateRequest animeCreateRequest) {

    // 1) Loại phim
    if (animeCreateRequest.getExpectedEpisodes() == 1) {
      Type movieType = typeRepository.findByNameAndType("Phim lẻ", "Loại phim")
          .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
      addTypeToAnime(anime, movieType.getId());
    } else {
      Type movieType = typeRepository.findByNameAndType("Phim bộ", "Loại phim")
          .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
      addTypeToAnime(anime, movieType.getId());
    }

    // 2) Tình trạng = "Sắp chiếu"
    Type statusType = typeRepository.findByNameAndType("Sắp chiếu", "Tình trạng")
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
    addTypeToAnime(anime, statusType.getId());

    // 3) Thể loại = "Anime"
    Type animeGenre = typeRepository.findByNameAndType("Anime", "Thể loại")
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
    addTypeToAnime(anime, animeGenre.getId());

    // 4) Năm phát hành => Tìm type theo name=(req.releaseYear.toString()), type="Năm phát hành"
    if (animeCreateRequest.getReleaseYear() != null) {
      Integer year = animeCreateRequest.getReleaseYear();
      if (year < 2014) {
        typeRepository.findByNameAndType("Trước 2014", "Năm phát hành")
            .ifPresent(t -> addTypeToAnime(anime, t.getId()));
      } else {
        String name = String.valueOf(animeCreateRequest.getReleaseYear());
        typeRepository.findByNameAndType(name, "Năm phát hành")
            .ifPresent(t -> addTypeToAnime(anime, t.getId()));
      }
    }

    // 5) Quốc gia
    if (animeCreateRequest.getCountryName() != null
        && !animeCreateRequest.getCountryName().isBlank()) {
      typeRepository.findByNameAndType(animeCreateRequest.getCountryName(), "Quốc gia")
          .ifPresentOrElse(t -> addTypeToAnime(anime, t.getId()), () -> {
                throw new AppException(ErrorCode.TYPE_NOT_FOUND);
              }
          );
    }

    // 6) Studio
    if (animeCreateRequest.getStudioName() != null
        && !animeCreateRequest.getStudioName().isBlank()) {
      typeRepository.findByNameAndType(animeCreateRequest.getStudioName(), "Studio")
          .ifPresentOrElse(t -> addTypeToAnime(anime, t.getId()), () -> {
                log.info("Studio error");

                throw new AppException(ErrorCode.TYPE_NOT_FOUND);
              }
          );
    }

    // 7) Lịch chiếu (dựa vào scheduleDate => "Thứ hai", "Thứ ba",...)
    if (animeCreateRequest.getScheduleDate() != null) {
      // Lấy dayOfWeek
      DayOfWeek dow = animeCreateRequest.getScheduleDate().getDayOfWeek();
      // dayOfWeek() trả về MONDAY, TUESDAY, ...
      String dayName = convertDayOfWeekToString(dow);

      // Tìm type => type= "Lịch chiếu phim", name = dayName
      typeRepository.findByNameAndType(dayName, "Lịch chiếu phim")
          .ifPresentOrElse(
              t -> addTypeToAnime(anime, t.getId()),
              () -> {
                throw new AppException(ErrorCode.TYPE_NOT_FOUND);
              }
          );
    }
  }

  /**
   * Thêm hàm gán 1 type cho Anime nếu pivot chưa tồn tại
   */
  void addTypeToAnime(Anime anime, String typeId) {

    // Tìm type
    Type type = typeRepository
        .findById(typeId)
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
    // Check pivot
    boolean existPivot = animeTypeRepository.existsByAnimeIdAndTypeId(anime.getId(), typeId);
    if (!existPivot) {
      AnimeType pivot = new AnimeType(anime, type);
      animeTypeRepository.save(pivot);
      // Đồng bộ list
      anime.getAnimeTypes().add(pivot);
    }
  }

  /**
   * Đồng bộ status = sắp chiếu, đang cập nhật, đã hoàn thành dựa vào totalEpisodes vs
   * expectedEpisodes.
   */
  // TODO Đặt thành biến typeName, typeType
  void syncAnimeStatusByEpisodeCount(Anime anime) {
    int total = (anime.getTotalEpisodes() == null) ? 0 : anime.getTotalEpisodes();
    int expected = (anime.getTotalEpisodes() == null) ? 0 : anime.getTotalEpisodes();

    String statusName;
    if (expected == 0) {
      statusName = "Sắp chiếu";
    } else if (total < expected) {
      statusName = "Đang cập nhật";
    } else {
      statusName = "Đã hoàn thành";
    }

    // Lấy Type Sắp chiếu / Đang cập nhật / Đã hoàn thành
    anime.getAnimeTypes().removeIf(at ->
        at.getType() != null
            && "Tình trạng".equalsIgnoreCase(at.getType().getType()));

    // Gắn type mới
    Type statusType = typeRepository.findByNameAndType(statusName, "Tình trạng")
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

    AnimeType pivot = new AnimeType(anime, statusType);
    animeTypeRepository.save(pivot);
    anime.getAnimeTypes().add(pivot);

    // Lưu DB
    animeRepository.save(anime);
  }

  String convertDayOfWeekToString(DayOfWeek dow) {
    return switch (dow) {
      case MONDAY -> "Thứ hai";
      case TUESDAY -> "Thứ ba";
      case WEDNESDAY -> "Thứ tư";
      case THURSDAY -> "Thứ năm";
      case FRIDAY -> "Thứ sáu";
      case SATURDAY -> "Thứ bảy";
      case SUNDAY -> "Chủ nhật";
      default -> null;
    };
  }

  void updateTypesForAnime(
      Anime anime,
      AnimeUpdateRequest req) {

    removeOldTypes(
        anime,
        List.of(MOVIE_TYPE, STATUS_TYPE, RELEASE_YEAR_TYPE, STUDIO_TYPE, GENRE_TYPE, SCHEDULE_TYPE,
            COUNTRY_TYPE));

    if (req.getMovieTypes() != null) {
      for (String name : req.getMovieTypes()) {
        addTypeToAnimeByName(anime, name, MOVIE_TYPE);
      }
    }
    if (req.getStatusNames() != null) {
      for (String name : req.getStatusNames()) {
        addTypeToAnimeByName(anime, name, STATUS_TYPE);
      }
    }
    if (req.getReleaseYearNames() != null) {
      for (String name : req.getReleaseYearNames()) {
        addTypeToAnimeByName(anime, name, RELEASE_YEAR_TYPE);
      }
    }
    if (req.getStudioNames() != null) {
      for (String name : req.getStudioNames()) {
        addTypeToAnimeByName(anime, name, STUDIO_TYPE);
      }
    }
    if (req.getGenreNames() != null) {
      for (String name : req.getGenreNames()) {
        addTypeToAnimeByName(anime, name, GENRE_TYPE);
      }
    }
    if (req.getScheduleNames() != null) {
      for (String name : req.getScheduleNames()) {
        addTypeToAnimeByName(anime, name, SCHEDULE_TYPE);
      }
    }
    if (req.getCountryNames() != null) {
      for (String name : req.getCountryNames()) {
        addTypeToAnimeByName(anime, name, COUNTRY_TYPE);
      }
    }

    animeRepository.save(anime);
  }

  void removeOldTypes(
      Anime anime,
      List<String> typeCategories) {
    // Lọc ra các AnimeType pivot có type.getType() thuộc các category => xoá
    List<AnimeType> removeList = anime.getAnimeTypes()
        .stream()
        .filter(at -> at.getType() != null
            && typeCategories.contains(at.getType().getType()))
        .toList();
    animeTypeRepository.deleteAll(removeList);
    anime.getAnimeTypes().removeAll(removeList);
  }

  void addTypeToAnimeByName(
      Anime anime,
      String typeName,
      String typeCategory) {
    // Tìm type
    Type t = typeRepository
        .findByNameAndType(typeName, typeCategory)
        .orElseThrow(
            () -> new AppException(ErrorCode.TYPE_NOT_FOUND)
        );

    // Check pivot
    boolean existPivot = animeTypeRepository.existsByAnimeIdAndTypeId(anime.getId(), t.getId());
    if (!existPivot) {
      animeTypeRepository.save(new AnimeType(anime, t));
      anime.getAnimeTypes().add(new AnimeType(anime, t));
    }
  }
}
