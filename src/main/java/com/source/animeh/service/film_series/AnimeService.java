package com.source.animeh.service.film_series;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.utils.MediaUtils.filterAndSortEpisodesByRole;

import com.source.animeh.constant.PredefinedStatus;
import com.source.animeh.dto.request.filter.AnimeFilterRequest;
import com.source.animeh.dto.request.filter.EpisodeFilterRequest;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.interface_package.service.film_series.AnimeServiceInterface;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.AnimeMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.user_interaction.WatchlistRepository;
import com.source.animeh.specification.film_series.AnimeSpecification;
import com.source.animeh.specification.film_series.EpisodeSpecification;
import com.source.animeh.utils.MediaUtils;
import com.source.animeh.utils.SecurityUtils;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnimeService implements AnimeServiceInterface {

  AnimeRepository animeRepository;
  AnimeMapper animeMapper;
  UserRepository userRepository;
  EpisodeRepository episodeRepository;
  EpisodeMapper episodeMapper;
  WatchlistRepository watchlistRepository;

  // Lấy top 4 phim có lượt view cao nhất
  @Override
  public List<AnimeResponse> getTop4Animes() {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Lấy top 4
    List<Anime> tops = MediaUtils
        .filterAnimesByRole(animeRepository.findTop4ByOrderByViewCountDesc(), user);
    User currentUser = SecurityUtils.getCurrentUser(userRepository);

    return tops.stream().map(anime -> {
      // Map sang AnimeResponse
      AnimeResponse dto = animeMapper.toAnimeResponse(anime, currentUser);

      // Tính tổng số tập
      int approvedCount = episodeRepository.countByAnimeIdAndStatus(
          anime.getId(),
          EPISODE_REQUEST_APPROVED.getStatus()
      );
      dto.setTotalEpisodeCount(approvedCount);

      // Kiểm tra đã ở trong watchlist hay chưa
      if (currentUser != null) {
        boolean inWatchlist = watchlistRepository
            .findByUserIdAndAnimeId(currentUser.getId(), anime.getId())
            .isPresent();
        dto.setInWatchlist(inWatchlist);
      }

      // ratingCount
      dto.setRatingCount(
          (anime.getRatingCount() == null) ? 0 : anime.getRatingCount()
      );

      // followCount
      long countFollow = watchlistRepository.countByAnimeId(anime.getId());
      dto.setFollowCount(countFollow);

      // Lấy episodes => filter => map
      List<Episode> allEps = anime.getEpisodes();
      if (allEps == null || allEps.isEmpty()) {
        // nếu không có tập => set list rỗng
        dto.setEpisodes(Collections.emptyList());
      } else {
        List<Episode> visibleEps = filterAndSortEpisodesByRole(allEps, currentUser);
        List<EpisodeInAnimeResponse> epDtos = visibleEps.stream()
            .map(ep -> animeMapper.episodeMapper()
                .toEpisodeInAnimeResponse(ep, currentUser))
            .toList();
        dto.setEpisodes(epDtos);
      }

      // Map series => nếu anime.getSeries() != null
      if (anime.getSeries() != null) {
        dto.setSeries(animeMapper.toAnimeSeriesInAnimeResponse(anime.getSeries()));
      } else {
        // hoặc đặt = null, tuỳ bạn
        dto.setSeries(null);
      }

      return dto;
    }).toList();
  }

  /**
   * Lọc Anime nâng cao theo: - Danh sách typeId (tagIds) - Năm phát hành - Rating tối thiểu -
   * nominated => true/false
   */
  @Override
  public Page<AnimeResponse> filterAnimeAdvanced(
      AnimeFilterRequest animeFilterRequest,
      int page,
      int size
  ) {
    User currentUser = SecurityUtils.getCurrentUser(userRepository);

    // Tạo spec => query
    Specification<Anime> spec = AnimeSpecification.buildSpecWithRole(animeFilterRequest,
        currentUser);

    Sort sort = Boolean.TRUE.equals(animeFilterRequest.getNewlyUpdated())
        ? Sort.by(Sort.Direction.DESC, "updatedAt")
        : Sort.by(Sort.Direction.DESC, "createdAt");

    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Anime> rawData = animeRepository.findAll(spec, pageable);

    // Map -> AnimeResponse, set episodes, series
    return rawData.map(anime -> {
      AnimeResponse dto = animeMapper.toAnimeResponse(anime, currentUser);

      // Tính tổng số tập
      int approvedCount = episodeRepository.countByAnimeIdAndStatus(
          anime.getId(),
          EPISODE_REQUEST_APPROVED.getStatus()
      );
      dto.setTotalEpisodeCount(approvedCount);

      // Kiểm tra đã ở trong watchlist hay chưa
      if (currentUser != null) {
        boolean inWatchlist = watchlistRepository
            .findByUserIdAndAnimeId(currentUser.getId(), anime.getId())
            .isPresent();
        dto.setInWatchlist(inWatchlist);
      }

      // ratingCount
      dto.setRatingCount(
          (anime.getRatingCount() == null) ? 0 : anime.getRatingCount()
      );

      // followCount
      long countFollow = watchlistRepository.countByAnimeId(anime.getId());
      dto.setFollowCount(countFollow);

      // episodes
      List<Episode> allEps = anime.getEpisodes();
      if (allEps == null || allEps.isEmpty()) {
        dto.setEpisodes(Collections.emptyList());
      } else {
        List<Episode> visible = filterAndSortEpisodesByRole(allEps, currentUser);
        List<EpisodeInAnimeResponse> epDtos = visible.stream()
            .map(ep -> animeMapper.episodeMapper()
                .toEpisodeInAnimeResponse(ep, currentUser))
            .toList();
        dto.setEpisodes(epDtos);
      }

      // series
      if (anime.getSeries() != null) {
        dto.setSeries(animeMapper.toAnimeSeriesInAnimeResponse(anime.getSeries()));
      }

      return dto;
    });
  }

  public Page<EpisodeResponse> filterEpisodeAdvanced(
      EpisodeFilterRequest req,
      int page,
      int size) {
    User currentUser = SecurityUtils.getCurrentUser(userRepository);

    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page, size, sort);

    Specification<Episode> spec = EpisodeSpecification.buildSpecWithRole(req, currentUser);

    Page<Episode> rawData = episodeRepository.findAll(spec, pageable);

    return rawData.map(ep -> episodeMapper.toEpisodeResponse(ep, currentUser));
  }

  /**
   * Lấy ngẫu nhiên 1 Anime (đã duyệt) trong hệ thống.
   *
   * @return AnimeResponse của bộ phim được chọn ngẫu nhiên
   */
  public AnimeResponse getRandomApprovedAnime() {

    User user = SecurityUtils.getCurrentUser(userRepository);

    List<Anime> approvedAnimes = animeRepository.findAll().stream()
        .filter(
            a -> PredefinedStatus.FILM_REQUEST_APPROVED.getStatus().equals(a.getStatusSubmitted()))
        .toList();

    if (approvedAnimes.isEmpty()) {
      return null;
    }

    // Random
    Random rand = new Random();
    Anime randomAnime = approvedAnimes.get(rand.nextInt(approvedAnimes.size()));

    // Map sang response
    AnimeResponse res = animeMapper.toAnimeResponse(randomAnime, user);

    // Tính tổng số tập
    int approvedCount = episodeRepository.countByAnimeIdAndStatus(
        randomAnime.getId(),
        EPISODE_REQUEST_APPROVED.getStatus()
    );
    res.setTotalEpisodeCount(approvedCount);

    // Kiểm tra xem anime đã có trong watchlist của user hay chưa
    if (user != null) {
      boolean inWatchlist = watchlistRepository
          .findByUserIdAndAnimeId(user.getId(), randomAnime.getId())
          .isPresent();
      res.setInWatchlist(inWatchlist);
    }

    // ratingCount
    res.setRatingCount(
        (randomAnime.getRatingCount() == null) ? 0 : randomAnime.getRatingCount()
    );

    // followCount
    long countFollow = watchlistRepository.countByAnimeId(randomAnime.getId());
    res.setFollowCount(countFollow);

    // Lấy danh sách episodes => set
    List<Episode> allEps = episodeRepository.findByAnimeId(randomAnime.getId());
    List<Episode> visibleEps = filterAndSortEpisodesByRole(allEps, user);

    // Map sang EpisodeInAnimeResponse
    List<EpisodeInAnimeResponse> epDtos = visibleEps.stream()
        .map(ep -> episodeMapper.toEpisodeInAnimeResponse(ep, user))
        .toList();
    res.setEpisodes(epDtos);

    // Series => map => set animes
    if (randomAnime.getSeries() != null) {
      AnimeSeriesInAnimeResponse seriesDto = animeMapper.toAnimeSeriesInAnimeResponse(
          randomAnime.getSeries());
      res.setSeries(seriesDto);
    }

    return res;
  }
}
