package com.source.animeh.mapper.user_interaction;

import com.source.animeh.dto.response.user_interaction.WatchlistResponse;
import com.source.animeh.entity.user_interaction.Watchlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper chuyển đổi giữa entity {@link Watchlist} và DTO {@link WatchlistResponse}.
 * <p>
 * Mapper này được sử dụng để chuyển đổi thông tin từ đối tượng Watchlist thành đối tượng
 * WatchlistResponse, bao gồm chuyển đổi các trường liên quan đến thông tin của anime.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface WatchlistMapper {

  // ===================== Các hằng số mapping =====================
  String ANIME_ID = "anime.id";
  String ANIME_TITLE = "anime.title";
  String ANIME_DESCRIPTION = "anime.description";
  String ANIME_TOTAL_EPISODES = "anime.totalEpisodes";
  String ANIME_VIEW_COUNT = "anime.viewCount";
  String ANIME_AVERAGE_RATING = "anime.averageRating";
  String ANIME_POSTER_URL = "anime.posterUrl";
  String ANIME_BANNER_URL = "anime.bannerUrl";

  /**
   * Chuyển đổi đối tượng {@link Watchlist} thành {@link WatchlistResponse}.
   *
   * @param watchlist đối tượng {@code Watchlist} cần chuyển đổi
   * @return đối tượng {@code WatchlistResponse} chứa thông tin chuyển đổi từ {@code Watchlist}
   */
  @Mapping(target = ANIME_ID, source = ANIME_ID)
  @Mapping(target = ANIME_TITLE, source = ANIME_TITLE)
  @Mapping(target = ANIME_DESCRIPTION, source = ANIME_DESCRIPTION)
  @Mapping(target = ANIME_TOTAL_EPISODES, source = ANIME_TOTAL_EPISODES)
  @Mapping(target = ANIME_VIEW_COUNT, source = ANIME_VIEW_COUNT)
  @Mapping(target = ANIME_AVERAGE_RATING, source = ANIME_AVERAGE_RATING)
  @Mapping(target = ANIME_POSTER_URL, source = ANIME_POSTER_URL)
  @Mapping(target = ANIME_BANNER_URL, source = ANIME_BANNER_URL)
  WatchlistResponse toWatchlistResponse(Watchlist watchlist);
}
