package com.source.animeh.mapper.film_series;

import com.source.animeh.dto.request.film_series.anime.AnimeCreateRequest;
import com.source.animeh.dto.request.film_series.anime.AnimeUpdateRequest;
import com.source.animeh.dto.response.episode.AnimeInEpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeInSeriesResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.PublishingScheduleInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.TypeItemResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeSeries;
import com.source.animeh.entity.film_series.AnimeType;
import com.source.animeh.entity.film_series.Type;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import com.source.animeh.mapper.episode.EpisodeMapper;
import com.source.animeh.mapper.film_series.schedule.PublishingScheduleMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper chuyển đổi giữa các đối tượng liên quan đến Anime và các DTO tương ứng.
 * <p>
 * Bao gồm các chuyển đổi:
 * <ul>
 *   <li>Chuyển {@link Anime} sang {@link AnimeResponse} và {@link AnimeInEpisodeResponse} (dùng trong EpisodeResponse)</li>
 *   <li>Chuyển từ {@link AnimeCreateRequest} và {@link AnimeUpdateRequest} sang {@link Anime}</li>
 *   <li>Hỗ trợ chuyển đổi danh sách {@link Episode} sang danh sách {@link EpisodeInAnimeResponse}</li>
 *   <li>Hỗ trợ mapping các thuộc tính liên quan đến {@code animeTypes} sang {@link TypeItemResponse}</li>
 *   <li>Chuyển {@link Anime} sang {@link AnimeInSeriesResponse} (dùng cho AnimeSeries)</li>
 *   <li>Chuyển {@link AnimeSeries} sang {@link AnimeSeriesInAnimeResponse}</li>
 * </ul>
 * Đồng thời áp dụng các bộ lọc thông tin theo vai trò người dùng.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {EpisodeMapper.class, PublishingScheduleMapper.class})
public interface AnimeMapper extends BaseRoleFilterMapper<Anime, AnimeResponse> {

  // ====================== Các hằng số mapping ======================
  // Hằng số mapping cho các field trong AnimeResponse, AnimeInEpisodeResponse
  String TARGET_EPISODES = "episodes";
  String TARGET_SERIES = "series";
  String TARGET_TYPE_ITEMS = "typeItems";
  String TARGET_SUBMITTED_BY = "submittedBy";
  String TARGET_REVIEWED_BY = "reviewedBy";

  String SOURCE_SUBMITTED_BY_USERNAME = "submittedBy.username";
  String SOURCE_REVIEWED_BY_USERNAME = "reviewedBy.username";
  String SOURCE_ANIME_TYPES = "animeTypes";
  String QUALIFIED_MAP_ANIME_TYPES = "mapAnimeTypes";

  // Hằng số cho mapping trong chuyển đổi từ request sang entity
  String FIELD_ID = "id";
  String FIELD_SERIES = "series";
  String FIELD_POSTER_URL = "posterUrl";
  String FIELD_BANNER_URL = "bannerUrl";
  String FIELD_AVERAGE_RATING = "averageRating";
  String FIELD_VIEW_COUNT = "viewCount";
  String FIELD_STATUS_SUBMITTED = "statusSubmitted";
  String FIELD_REJECTED_REASON = "rejectedReason";
  String FIELD_SUBMITTED_BY = "submittedBy";
  String FIELD_REVIEWED_BY = "reviewedBy";
  String FIELD_SUBMITTED_AT = "submittedAt";
  String FIELD_REVIEWED_AT = "reviewedAt";
  String FIELD_CREATED_AT = "createdAt";
  String FIELD_UPDATED_AT = "updatedAt";
  String FIELD_SERIES_ORDER = "seriesOrder";
  String FIELD_ANIME_TYPES = "animeTypes";
  String FIELD_RELEASE_YEAR = "releaseYear";
  String FIELD_TRAILER_URL = "trailerUrl";
  String FIELD_EXPECTED_EPISODES = "expectedEpisodes";

  // Hằng số cho mapping từ request: trailerUrl được lấy từ linkTrailer
  String SOURCE_LINK_TRAILER = "linkTrailer";

  // ====================== Các phương thức mapping ======================

  /**
   * Chuyển đổi đối tượng {@link Anime} sang {@link AnimeResponse} kèm thông tin người dùng hiện
   * tại.
   *
   * @param anime       đối tượng {@code Anime} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại (để áp dụng các filter nếu cần)
   * @return đối tượng {@code AnimeResponse} chứa thông tin của anime
   */
  @Mapping(target = TARGET_EPISODES, ignore = true)
  @Mapping(target = TARGET_SERIES, expression = "java(toAnimeSeriesInAnimeResponse(anime.getSeries()))")
  @Mapping(target = TARGET_TYPE_ITEMS, source = SOURCE_ANIME_TYPES, qualifiedByName = QUALIFIED_MAP_ANIME_TYPES)
  @Mapping(source = SOURCE_SUBMITTED_BY_USERNAME, target = TARGET_SUBMITTED_BY)
  @Mapping(source = SOURCE_REVIEWED_BY_USERNAME, target = TARGET_REVIEWED_BY)
  @Mapping(target = "publishingSchedule",
      source = "publishingSchedule", qualifiedByName = "mapScheduleInAnimeResponse")
  @Mapping(target = "nextEpisodePublishingDate", source = "nextEpisodePublishingDate")
  AnimeResponse toAnimeResponse(Anime anime, @Context User currentUser);

  @Named("mapScheduleInAnimeResponse")
  PublishingScheduleInAnimeResponse toScheduleInAnimeResponse(PublishingSchedule entity);


  @Named("mapAnimePublishResponse")
  @Mapping(target = TARGET_EPISODES, ignore = true)
  @Mapping(target = TARGET_SERIES, expression = "java(toAnimeSeriesInAnimeResponse(anime.getSeries()))")
  @Mapping(target = TARGET_TYPE_ITEMS, source = SOURCE_ANIME_TYPES, qualifiedByName = QUALIFIED_MAP_ANIME_TYPES)
  @Mapping(source = SOURCE_SUBMITTED_BY_USERNAME, target = TARGET_SUBMITTED_BY)
  @Mapping(source = SOURCE_REVIEWED_BY_USERNAME, target = TARGET_REVIEWED_BY)
  @Mapping(target = "nextEpisodePublishingDate", source = "nextEpisodePublishingDate")
  AnimeResponse toAnimePublishResponse(Anime anime, @Context User currentUser);

  /**
   * Chuyển đổi đối tượng {@link Anime} sang {@link AnimeInEpisodeResponse} (dùng cho
   * EpisodeResponse.anime) kèm thông tin người dùng hiện tại.
   *
   * @param anime       đối tượng {@code Anime} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại
   * @return đối tượng {@code AnimeInEpisodeResponse} chứa thông tin rút gọn của anime
   */
  @Named("mapAnimeInEpisode")
  @Mapping(target = TARGET_TYPE_ITEMS, source = SOURCE_ANIME_TYPES, qualifiedByName = QUALIFIED_MAP_ANIME_TYPES)
  @Mapping(target = TARGET_SERIES, expression = "java(toAnimeSeriesInAnimeResponse(anime.getSeries()))")
  AnimeInEpisodeResponse toAnimeInEpisodeResponse(Anime anime, @Context User currentUser);

  /**
   * Chuyển đổi đối tượng {@link AnimeCreateRequest} sang {@link Anime}.
   * <p>
   * Các trường sau sẽ được ignore: id, series, posterUrl, bannerUrl, averageRating, viewCount,
   * statusSubmitted, rejectedReason, submittedBy, reviewedBy, submittedAt, reviewedAt, createdAt,
   * updatedAt, seriesOrder, animeTypes.
   * </p>
   *
   * @param animeCreateRequest đối tượng chứa thông tin tạo anime
   * @return đối tượng {@code Anime} được tạo từ {@code AnimeCreateRequest}
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = "nextEpisodePublishingDate", source = "nextEpisodePublishingDate")
  @Mapping(target = FIELD_SERIES, ignore = true)
  @Mapping(target = FIELD_POSTER_URL, ignore = true)
  @Mapping(target = FIELD_BANNER_URL, ignore = true)
  @Mapping(target = FIELD_AVERAGE_RATING, ignore = true)
  @Mapping(target = FIELD_VIEW_COUNT, ignore = true)
  @Mapping(target = FIELD_STATUS_SUBMITTED, ignore = true)
  @Mapping(target = FIELD_REJECTED_REASON, ignore = true)
  @Mapping(target = FIELD_SUBMITTED_BY, ignore = true)
  @Mapping(target = FIELD_REVIEWED_BY, ignore = true)
  @Mapping(target = FIELD_SUBMITTED_AT, ignore = true)
  @Mapping(target = FIELD_REVIEWED_AT, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  @Mapping(target = FIELD_SERIES_ORDER, ignore = true)
  @Mapping(target = FIELD_ANIME_TYPES, ignore = true)
  @Mapping(target = FIELD_RELEASE_YEAR, source = FIELD_RELEASE_YEAR)
  @Mapping(target = FIELD_TRAILER_URL, source = SOURCE_LINK_TRAILER)
  @Mapping(target = FIELD_EXPECTED_EPISODES, source = FIELD_EXPECTED_EPISODES)
  Anime toAnime(AnimeCreateRequest animeCreateRequest);

  /**
   * Cập nhật thông tin của đối tượng {@link Anime} từ {@link AnimeUpdateRequest}.
   * <p>
   * Áp dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác null. Các trường
   * ignore: posterUrl, bannerUrl, series, animeTypes, updatedAt.
   * </p>
   *
   * @param anime              đối tượng {@code Anime} cần cập nhật
   * @param animeUpdateRequest đối tượng chứa thông tin cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_POSTER_URL, ignore = true)
  @Mapping(target = FIELD_BANNER_URL, ignore = true)
  @Mapping(target = FIELD_SERIES, ignore = true)
  @Mapping(target = FIELD_ANIME_TYPES, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  @Mapping(target = FIELD_RELEASE_YEAR, source = FIELD_RELEASE_YEAR)
  @Mapping(target = "director", source = "director")
  @Mapping(target = FIELD_EXPECTED_EPISODES, source = FIELD_EXPECTED_EPISODES)
  @Mapping(target = FIELD_TRAILER_URL, source = FIELD_TRAILER_URL)
  @Mapping(target = FIELD_SERIES_ORDER, source = FIELD_SERIES_ORDER)
  @Mapping(target = "nextEpisodePublishingDate", source = "nextEpisodePublishingDate")
  void updateAnime(@MappingTarget Anime anime, AnimeUpdateRequest animeUpdateRequest);

  /**
   * Chuyển đổi danh sách {@link Episode} sang danh sách {@link EpisodeInAnimeResponse}.
   *
   * @param episodes    danh sách {@code Episode} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại
   * @return danh sách {@code EpisodeInAnimeResponse} tương ứng; nếu danh sách đầu vào null thì trả
   * về danh sách rỗng
   */
  default List<EpisodeInAnimeResponse> toEpisodeInAnimeResponseList(List<Episode> episodes,
      @Context User currentUser) {
    if (episodes == null) {
      return Collections.emptyList();
    }
    return episodes.stream()
        .map(ep -> episodeMapper().toEpisodeInAnimeResponse(ep, currentUser))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Trả về instance của {@link EpisodeMapper} được MapStruct tạo ra.
   *
   * @return instance của {@code EpisodeMapper}
   */
  default EpisodeMapper episodeMapper() {
    return Mappers.getMapper(EpisodeMapper.class);
  }

  // ====================== Các phương thức hỗ trợ chuyển đổi ======================

  /**
   * Chuyển đổi danh sách {@link AnimeType} sang danh sách {@link TypeItemResponse}.
   *
   * @param animeTypes danh sách {@code AnimeType} cần chuyển đổi
   * @return danh sách {@code TypeItemResponse}; nếu danh sách đầu vào null thì trả về danh sách
   * rỗng
   */
  @Named(QUALIFIED_MAP_ANIME_TYPES)
  default List<TypeItemResponse> mapAnimeTypes(List<AnimeType> animeTypes) {
    if (animeTypes == null) {
      return Collections.emptyList();
    }
    return animeTypes.stream()
        .filter(Objects::nonNull)
        .map(at -> {
          if (at.getType() == null) {
            return null;
          }
          Type t = at.getType();
          TypeItemResponse dto = new TypeItemResponse();
          dto.setId(t.getId());
          dto.setName(t.getName());
          dto.setType(t.getType());
          return dto;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Chuyển đổi đối tượng {@link Anime} sang {@link AnimeInSeriesResponse} dùng cho đối tượng
   * {@link AnimeSeries}.
   *
   * @param anime đối tượng {@code Anime} cần chuyển đổi
   * @return đối tượng {@code AnimeInSeriesResponse} chứa thông tin rút gọn của anime; nếu
   * {@code anime} null trả về null
   */
  default AnimeInSeriesResponse toAnimeInSeriesResponse(Anime anime) {
    if (anime == null) {
      return null;
    }
    AnimeInSeriesResponse dto = new AnimeInSeriesResponse();
    dto.setId(anime.getId());
    dto.setTitle(anime.getTitle());
    dto.setSeriesOrder(anime.getSeriesOrder());
    dto.setPosterUrl(anime.getPosterUrl());
    dto.setBannerUrl(anime.getBannerUrl());
    dto.setAverageRating(anime.getAverageRating());
    dto.setViewCount(anime.getViewCount());
    return dto;
  }

  /**
   * Chuyển đổi đối tượng {@link AnimeSeries} sang {@link AnimeSeriesInAnimeResponse} (dạng rút
   * gọn).
   *
   * @param series đối tượng {@code AnimeSeries} cần chuyển đổi
   * @return đối tượng {@code AnimeSeriesInAnimeResponse} chứa thông tin rút gọn của series; nếu
   * {@code series} null trả về null
   */
  default AnimeSeriesInAnimeResponse toAnimeSeriesInAnimeResponse(AnimeSeries series) {
    if (series == null) {
      return null;
    }
    AnimeSeriesInAnimeResponse dto = new AnimeSeriesInAnimeResponse();
    dto.setId(series.getId());
    dto.setTitle(series.getTitle());
    dto.setPosterUrl(series.getPosterUrl());
    dto.setBannerUrl(series.getBannerUrl());
    return dto;
  }

  // ====================== Các phương thức lọc thông tin theo vai trò ======================

  /**
   * Lọc thông tin của {@link AnimeResponse} khi hiển thị cho người dùng.
   * <p>
   * Các trường nhạy cảm sẽ bị ẩn (submittedBy, reviewedBy, submittedAt, reviewedAt, createdAt,
   * updatedAt).
   * </p>
   *
   * @param dto đối tượng {@code AnimeResponse} cần lọc thông tin
   */
  @Override
  default void filterForUser(AnimeResponse dto) {
    BaseRoleFilterMapper.super.filterForUser(dto);
    dto.getPublishingSchedule().setId(null);
    dto.getPublishingSchedule().setModDeadline(null);
    dto.getPublishingSchedule().setStatusDeadline(null);
    dto.getPublishingSchedule().setCreatedAt(null);
    dto.getPublishingSchedule().setUpdatedAt(null);
    dto.setSubmittedBy(null);
    dto.setReviewedBy(null);
    dto.setSubmittedAt(null);
    dto.setReviewedAt(null);
    dto.setCreatedAt(null);
    dto.setUpdatedAt(null);
  }

  /**
   * Lọc thông tin của {@link AnimeResponse} khi hiển thị cho moderator.
   *
   * @param dto đối tượng {@code AnimeResponse} cần lọc thông tin
   */
  @Override
  default void filterForModerator(AnimeResponse dto) {
    BaseRoleFilterMapper.super.filterForModerator(dto);
  }
}
