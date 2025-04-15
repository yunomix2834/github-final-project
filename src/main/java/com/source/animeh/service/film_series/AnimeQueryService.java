package com.source.animeh.service.film_series;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.utils.MediaUtils.filterAndSortEpisodesByRole;
import static com.source.animeh.utils.MediaUtils.filterEpisodesByRole;

import com.source.animeh.dto.response.episode.AnimeInEpisodeResponse;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeSeries;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.film_series.AnimeQueryServiceInterface;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.AnimeMapper;
import com.source.animeh.mapper.film_series.AnimeSeriesMapper;
import com.source.animeh.mapper.film_series.schedule.PublishingScheduleMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.AnimeSeriesRepository;
import com.source.animeh.repository.user_interaction.ViewingHistoryRepository;
import com.source.animeh.repository.user_interaction.WatchlistRepository;
import com.source.animeh.specification.film_series.AnimeSpecification;
import com.source.animeh.utils.MediaUtils;
import com.source.animeh.utils.SecurityUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
public class AnimeQueryService implements AnimeQueryServiceInterface {

  AnimeSeriesRepository animeSeriesRepository;
  AnimeRepository animeRepository;
  EpisodeRepository episodeRepository;

  AnimeSeriesMapper animeSeriesMapper;
  AnimeMapper animeMapper;
  EpisodeMapper episodeMapper;

  UserRepository userRepository;
  WatchlistRepository watchlistRepository;
  ViewingHistoryRepository viewingHistoryRepository;
  PublishingScheduleMapper publishingScheduleMapper;

  // Lấy tất cả Series
  @Override
  public List<AnimeSeriesResponse> getAllSeries() {
    User user = SecurityUtils.getCurrentUser(userRepository);

    List<AnimeSeries> listSeries = animeSeriesRepository.findAll();

    return listSeries
        .stream()
        .map(animeSeries -> {

          // Filter animes cho series nayf
          List<Anime> filteredAnimes = MediaUtils
              .filterAnimesByRole(animeSeries.getAnimes(), user);

          AnimeSeriesResponse animeSeriesResponse = animeSeriesMapper.toAnimeSeriesResponse(
              animeSeries, user);

          // Map sub-animes
          animeSeriesResponse
              .setAnimes(animeSeriesMapper
                  .mapSubAnimes(filteredAnimes)
                  .stream()
                  .peek(dto -> {
                    if (user != null) {
                      boolean inWatchlist = watchlistRepository
                          .findByUserIdAndAnimeId(user.getId(), dto.getId())
                          .isPresent();
                      dto.setInWatchlist(inWatchlist);
                    }
                  })
                  .collect(Collectors.toList()));
          return animeSeriesResponse;
        })
        .collect(Collectors.toList());
  }

  // Lấy 1 Series
  @Override
  public AnimeSeriesResponse getSeriesById(
      String seriesId) {
    User user = SecurityUtils.getCurrentUser(userRepository);

    AnimeSeries animeSeries = animeSeriesRepository
        .findById(seriesId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND)
        );

    // Filter anime trong series
    List<Anime> filteredAnimes = MediaUtils
        .filterAnimesByRole(animeSeries.getAnimes(), user);

    AnimeSeriesResponse res = animeSeriesMapper
        .toAnimeSeriesResponse(animeSeries, user);
    res.setAnimes(animeSeriesMapper
        .mapSubAnimes(filteredAnimes)
        .stream()
        .peek(dto -> {
          if (user != null) {
            boolean inWatchlist = watchlistRepository
                .findByUserIdAndAnimeId(user.getId(), dto.getId())
                .isPresent();
            dto.setInWatchlist(inWatchlist);
          }
        })
        .collect(Collectors.toList()));

    return res;
  }

  // Lấy tất cả Anime trong 1 Series
  public List<AnimeResponse> getAnimesInSeries(String seriesId) {
    User user = SecurityUtils.getCurrentUser(userRepository);

    AnimeSeries animeSeries = animeSeriesRepository
        .findById(seriesId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_SERIES_NOT_FOUND)
        );

    List<Anime> animes = animeSeries.getAnimes();
    if (animes == null || animes.isEmpty()) {
      return Collections.emptyList();
    }

    return animes.stream().map(anime -> {
      // Map
      AnimeResponse res = animeMapper.toAnimeResponse(anime, user);

      // Đếm số tập Approved
      int approvedCount = episodeRepository.countByAnimeIdAndStatus(
          anime.getId(),
          EPISODE_REQUEST_APPROVED.getStatus() // ví dụ "APPROVED"
      );
      res.setTotalEpisodeCount(approvedCount);

      // Check Watchlist
      if (user != null) {
        boolean inWatchlist = watchlistRepository
            .findByUserIdAndAnimeId(user.getId(), anime.getId())
            .isPresent();
        res.setInWatchlist(inWatchlist);
      }

      // ratingCount
      res.setRatingCount(
          (anime.getRatingCount() == null) ? 0 : anime.getRatingCount()
      );

      // followCount
      long countFollow = watchlistRepository.countByAnimeId(anime.getId());
      res.setFollowCount(countFollow);

      // episodes => filter => map
      List<Episode> allEps = (anime.getEpisodes() == null)
          ? Collections.emptyList()
          : anime.getEpisodes();
      List<Episode> visibleEps = filterEpisodesByRole(allEps, user);
      List<EpisodeInAnimeResponse> epDtos = visibleEps
          .stream()
          .map(
              ep -> episodeMapper
                  .toEpisodeInAnimeResponse(ep, user)
          )
          .toList();
      res.setEpisodes(epDtos);

      // series (rút gọn)
      res.setSeries(animeMapper
          .toAnimeSeriesInAnimeResponse(animeSeries));
      return res;
    }).toList();
  }

  // Lấy tất cả Anime
  @Override
  public Page<AnimeResponse> getAllAnime(
      int page,
      int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Specification<Anime> spec = AnimeSpecification
        .filterByRole(user);

    Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by("createdAt").descending()
    );

    Page<Anime> rawPage = animeRepository
        .findAll(spec, pageable);

    return rawPage.map(anime -> {

      // Map Anime -> AnimeResponse
      AnimeResponse animeResponse = animeMapper
          .toAnimeResponse(anime, user);

      // Đếm số tập Approved
      int approvedCount = episodeRepository.countByAnimeIdAndStatus(
          anime.getId(),
          EPISODE_REQUEST_APPROVED.getStatus() // ví dụ "APPROVED"
      );
      animeResponse.setTotalEpisodeCount(approvedCount);

      // Kiểm tra đã ở trong watchlist hay chưa
      if (user != null) {
        boolean inWatchlist = watchlistRepository
            .findByUserIdAndAnimeId(user.getId(), anime.getId())
            .isPresent();
        animeResponse.setInWatchlist(inWatchlist);
      }

      // ratingCount
      animeResponse.setRatingCount(
          (anime.getRatingCount() == null) ? 0 : anime.getRatingCount()
      );

      // followCount
      long countFollow = watchlistRepository.countByAnimeId(anime.getId());
      animeResponse.setFollowCount(countFollow);

      // Lấy toàn bộ Episode
      List<Episode> allEps = anime.getEpisodes();
      if (allEps != null && !allEps.isEmpty()) {
        // Lọc theo role => user chỉ thấy 'approved'
        List<Episode> visibleEps = filterAndSortEpisodesByRole(allEps, user);

        // Map sang EpisodeInAnimeResponse
        List<EpisodeInAnimeResponse> epResponses = visibleEps.stream()
            .map(ep -> episodeMapper.toEpisodeInAnimeResponse(ep, user))
            .toList();

        animeResponse.setEpisodes(epResponses);
      } else {
        animeResponse.setEpisodes(Collections.emptyList());
      }

      // Map Series rút gọn
      if (anime.getSeries() != null) {
        AnimeSeriesInAnimeResponse animeSeriesInAnimeResponse =
            animeMapper.toAnimeSeriesInAnimeResponse(
                anime.getSeries());
        animeResponse.setSeries(animeSeriesInAnimeResponse);
      }

      return animeResponse;
    });
  }

  // Lấy 1 Anime
  @Override
  public AnimeResponse getAnimeById(
      String animeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Anime anime = animeRepository
        .findById(animeId)
        .orElseThrow(() -> new AppException(ErrorCode.ANIME_NOT_FOUND));

    // Map sang response
    AnimeResponse res = animeMapper.toAnimeResponse(anime, user);

    // Tính tổng số tập
    int approvedCount = episodeRepository.countByAnimeIdAndStatus(
        animeId,
        EPISODE_REQUEST_APPROVED.getStatus()
    );
    res.setTotalEpisodeCount(approvedCount);

    // Kiểm tra xem anime đã có trong watchlist của user hay chưa
    if (user != null) {
      boolean inWatchlist = watchlistRepository
          .findByUserIdAndAnimeId(user.getId(), anime.getId())
          .isPresent();
      res.setInWatchlist(inWatchlist);
    }

    // ratingCount
    res.setRatingCount(
        (anime.getRatingCount() == null) ? 0 : anime.getRatingCount()
    );

    // followCount
    long countFollow = watchlistRepository.countByAnimeId(anime.getId());
    res.setFollowCount(countFollow);

    // Lấy danh sách episodes => set
    List<Episode> allEps = episodeRepository.findByAnimeId(animeId);
    List<Episode> visibleEps = filterAndSortEpisodesByRole(allEps, user);

    // Map sang EpisodeInAnimeResponse
    List<EpisodeInAnimeResponse> epDtos = visibleEps.stream()
        .map(ep -> episodeMapper.toEpisodeInAnimeResponse(ep, user))
        .toList();
    res.setEpisodes(epDtos);

    // Series => map => set animes
    if (anime.getSeries() != null) {
      AnimeSeriesInAnimeResponse seriesDto = animeMapper.toAnimeSeriesInAnimeResponse(
          anime.getSeries());
      res.setSeries(seriesDto);
    }

    return res;
  }

  // Lấy tất cả Episode của 1 Anime
  @Override
  public List<EpisodeResponse> getEpisodesOfAnime(String animeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Lấy tất cả tập của Anime
    List<Episode> allEpisodes = episodeRepository
        .findByAnimeId(animeId);

    // Filter theo role
    List<Episode> visibleEps = filterAndSortEpisodesByRole(allEpisodes, user);

    // Map sang EpisodeResponse
    return visibleEps.stream()
        .map(
            ep -> {
              EpisodeResponse epDto = episodeMapper.toEpisodeResponse(ep, user);
              AnimeInEpisodeResponse animeRes = epDto.getAnime();

              if (user != null) {
                boolean inWatchlist = watchlistRepository
                    .findByUserIdAndAnimeId(user.getId(), animeRes.getId())
                    .isPresent();
                animeRes.setInWatchlist(inWatchlist);
              }

              if (user != null) {
                viewingHistoryRepository
                    .findByUserIdAndEpisodeIdAndIsDeletedFalse(user.getId(), ep.getId())
                    .ifPresent(vh -> epDto.setHistoryDuration(vh.getWatchedDuration()));
              }

              epDto.setAnime(animeRes);
              return epDto;
            }
        )
        .collect(Collectors.toList());
  }

  // Lấy 1 Episode
  @Override
  public EpisodeResponse getEpisodeById(String episodeId) {
    User user = SecurityUtils.getCurrentUser(userRepository);

    Episode ep = episodeRepository
        .findById(episodeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EPISODE_NOT_FOUND)
        );

    EpisodeResponse epDto = episodeMapper
        .toEpisodeResponse(ep, user);

    AnimeInEpisodeResponse animeRes = epDto.getAnime();

    if (user != null) {
      boolean inWatchlist = watchlistRepository
          .findByUserIdAndAnimeId(user.getId(), animeRes.getId())
          .isPresent();
      animeRes.setInWatchlist(inWatchlist);
    }

    if (user != null) {
      viewingHistoryRepository
          .findByUserIdAndEpisodeIdAndIsDeletedFalse(user.getId(), ep.getId())
          .ifPresent(vh -> epDto.setHistoryDuration(vh.getWatchedDuration()));
    }

    epDto.setAnime(animeRes);

    return epDto;
  }
}
