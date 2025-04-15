package com.source.animeh.mapper.user_interaction;

import com.source.animeh.dto.response.user_interaction.ViewingHistoryResponse;
import com.source.animeh.entity.user_interaction.ViewingHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper chuyển đổi đối tượng {@link ViewingHistory} sang {@link ViewingHistoryResponse}.
 * <p>
 * Mapper này chuyển đổi các trường liên quan đến thông tin của tập phim (episode) và anime trong
 * lịch sử xem.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ViewingHistoryMapper {

  // ====================== Các hằng số mapping ======================
  // Các hằng số cho thông tin của Episode
  String EPISODE_ID = "episode.id";
  String EPISODE_EPISODE_NUMBER = "episode.episodeNumber";
  String EPISODE_VIDEO_URL = "episode.videoUrl";
  String EPISODE_DURATION = "episode.duration";

  // Các hằng số cho thông tin của Anime nằm trong Episode
  String EPISODE_ANIME_ID = "episode.anime.id";
  String EPISODE_ANIME_TITLE = "episode.anime.title";
  String EPISODE_ANIME_DESCRIPTION = "episode.anime.description";
  String EPISODE_ANIME_TOTAL_EPISODES = "episode.anime.totalEpisodes";
  String EPISODE_ANIME_VIEW_COUNT = "episode.anime.viewCount";
  String EPISODE_ANIME_AVERAGE_RATING = "episode.anime.averageRating";
  String EPISODE_ANIME_POSTER_URL = "episode.anime.posterUrl";
  String EPISODE_ANIME_BANNER_URL = "episode.anime.bannerUrl";

  /**
   * Chuyển đổi đối tượng {@link ViewingHistory} sang {@link ViewingHistoryResponse}.
   *
   * @param entity đối tượng {@code ViewingHistory} cần chuyển đổi
   * @return đối tượng {@code ViewingHistoryResponse} chứa thông tin chuyển đổi từ
   * {@code ViewingHistory}
   */
  @Mapping(target = EPISODE_ID, source = EPISODE_ID)
  @Mapping(target = EPISODE_EPISODE_NUMBER, source = EPISODE_EPISODE_NUMBER)
  @Mapping(target = EPISODE_VIDEO_URL, source = EPISODE_VIDEO_URL)
  @Mapping(target = EPISODE_DURATION, source = EPISODE_DURATION)
  @Mapping(target = EPISODE_ANIME_ID, source = EPISODE_ANIME_ID)
  @Mapping(target = EPISODE_ANIME_TITLE, source = EPISODE_ANIME_TITLE)
  @Mapping(target = EPISODE_ANIME_DESCRIPTION, source = EPISODE_ANIME_DESCRIPTION)
  @Mapping(target = EPISODE_ANIME_TOTAL_EPISODES, source = EPISODE_ANIME_TOTAL_EPISODES)
  @Mapping(target = EPISODE_ANIME_VIEW_COUNT, source = EPISODE_ANIME_VIEW_COUNT)
  @Mapping(target = EPISODE_ANIME_AVERAGE_RATING, source = EPISODE_ANIME_AVERAGE_RATING)
  @Mapping(target = EPISODE_ANIME_POSTER_URL, source = EPISODE_ANIME_POSTER_URL)
  @Mapping(target = EPISODE_ANIME_BANNER_URL, source = EPISODE_ANIME_BANNER_URL)
  ViewingHistoryResponse toViewingHistoryResponse(ViewingHistory entity);
}
