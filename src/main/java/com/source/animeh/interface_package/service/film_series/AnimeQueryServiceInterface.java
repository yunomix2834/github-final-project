package com.source.animeh.interface_package.service.film_series;

import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AnimeQueryServiceInterface {

  // Lấy tất cả Series
  List<AnimeSeriesResponse> getAllSeries();

  // Lấy 1 Series
  AnimeSeriesResponse getSeriesById(
      String seriesId);

  // Lấy tất cả Anime
  Page<AnimeResponse> getAllAnime(
      int page,
      int size);

  // Lấy 1 Anime
  AnimeResponse getAnimeById(
      String animeId);

  // Lấy tất cả Episode của 1 Anime
  List<EpisodeResponse> getEpisodesOfAnime(String animeId);

  // Lấy 1 Episode
  EpisodeResponse getEpisodeById(String episodeId);

  // Lấy tất cả Anime trong 1 Series
  List<AnimeResponse> getAnimesInSeries(String seriesId);
}
