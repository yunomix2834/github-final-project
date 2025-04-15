package com.source.animeh.mapper.episode;

import com.source.animeh.dto.request.film_series.episode.EpisodeCreateRequest;
import com.source.animeh.dto.request.film_series.episode.EpisodeUpdateRequest;
import com.source.animeh.dto.response.episode.EpisodePublishingScheduleInEpisodeResponse;
import com.source.animeh.dto.response.episode.EpisodeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import com.source.animeh.mapper.film_series.AnimeMapper;
import com.source.animeh.mapper.film_series.schedule.EpisodePublishingScheduleMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper chuyển đổi giữa các đối tượng liên quan đến tập phim (Episode).
 * <p>
 * Mapper này chuyển đổi từ các request sang entity, từ entity sang các response và áp dụng các bộ
 * lọc thông tin theo vai trò người dùng.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {AnimeMapper.class,
    EpisodePublishingScheduleMapper.class})
public interface EpisodeMapper extends BaseRoleFilterMapper<Episode, EpisodeResponse> {

  // ----------------- Các hằng số dùng cho mapping -----------------

  // Các hằng số cho source và target mapping của username
  String SOURCE_SUBMITTED_BY_USERNAME = "submittedBy.username";
  String SOURCE_REVIEWED_BY_USERNAME = "reviewedBy.username";
  String TARGET_SUBMITTED_BY = "submittedBy";
  String TARGET_REVIEWED_BY = "reviewedBy";

  // Hằng số cho mapping field anime với qualified name
  String SOURCE_ANIME = "anime";
  String QUALIFIED_MAP_ANIME_IN_EPISODE = "mapAnimeInEpisode";
  String FIELD_ANIME = "anime";

  // Các hằng số cho ignore mapping khi tạo hoặc cập nhật Episode
  String FIELD_ID = "id";
  String FIELD_VIDEO_URL = "videoUrl";
  String FIELD_STATUS = "status";
  String FIELD_SUBMITTED_BY = "submittedBy";
  String FIELD_REVIEWED_BY = "reviewedBy";
  String FIELD_SUBMITTED_AT = "submittedAt";
  String FIELD_REVIEWED_AT = "reviewedAt";
  String FIELD_CREATED_AT = "createdAt";
  String FIELD_UPDATED_AT = "updatedAt";

  // ----------------- Các phương thức mapping -----------------

  /**
   * Chuyển đổi đối tượng {@link Episode} sang {@link EpisodeResponse} với thông tin của người dùng
   * hiện tại.
   *
   * @param entity      đối tượng {@code Episode} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại
   * @return đối tượng {@code EpisodeResponse} sau khi chuyển đổi
   */
  @Mapping(source = SOURCE_SUBMITTED_BY_USERNAME, target = TARGET_SUBMITTED_BY)
  @Mapping(source = SOURCE_REVIEWED_BY_USERNAME, target = TARGET_REVIEWED_BY)
  @Mapping(target = FIELD_ANIME, source = SOURCE_ANIME, qualifiedByName = QUALIFIED_MAP_ANIME_IN_EPISODE)
  @Mapping(target = "episodePublishingSchedule",
      source = "episodeSchedule", qualifiedByName = "mapEpisodeScheduleInEpisodeResponse")
  EpisodeResponse toEpisodeResponse(Episode entity, @Context User currentUser);

  @Named("mapEpisodeScheduleInEpisodeResponse")
  EpisodePublishingScheduleInEpisodeResponse toEpisodeScheduleInEpisodeResponse(
      EpisodePublishingSchedule entity);

  @Named("mapEpisodePublishResponse")
  @Mapping(source = SOURCE_SUBMITTED_BY_USERNAME, target = TARGET_SUBMITTED_BY)
  @Mapping(source = SOURCE_REVIEWED_BY_USERNAME, target = TARGET_REVIEWED_BY)
  @Mapping(target = FIELD_ANIME, source = SOURCE_ANIME, qualifiedByName = QUALIFIED_MAP_ANIME_IN_EPISODE)
  EpisodeResponse toEpisodePublishResponse(Episode entity, @Context User currentUser);

  /**
   * Chuyển đổi đối tượng {@link Episode} sang {@link EpisodeInAnimeResponse} với thông tin của
   * người dùng hiện tại.
   *
   * @param entity      đối tượng {@code Episode} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại
   * @return đối tượng {@code EpisodeInAnimeResponse} chứa thông tin tập phim trong anime
   */
  @Mapping(target = TARGET_SUBMITTED_BY, source = SOURCE_SUBMITTED_BY_USERNAME)
  @Mapping(target = TARGET_REVIEWED_BY, source = SOURCE_REVIEWED_BY_USERNAME)
  EpisodeInAnimeResponse toEpisodeInAnimeResponse(Episode entity, @Context User currentUser);

  /**
   * Chuyển đổi đối tượng {@link EpisodeCreateRequest} sang {@link Episode}.
   * <p>
   * Các trường được ignore không được mapping từ {@code EpisodeCreateRequest}.
   * </p>
   *
   * @param episodeCreateRequest đối tượng chứa thông tin tạo tập phim
   * @return đối tượng {@code Episode} được tạo từ {@code EpisodeCreateRequest}
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = FIELD_ANIME, ignore = true)
  @Mapping(target = FIELD_VIDEO_URL, ignore = true)
  @Mapping(target = FIELD_STATUS, ignore = true)
  @Mapping(target = FIELD_SUBMITTED_BY, ignore = true)
  @Mapping(target = FIELD_REVIEWED_BY, ignore = true)
  @Mapping(target = FIELD_SUBMITTED_AT, ignore = true)
  @Mapping(target = FIELD_REVIEWED_AT, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  Episode toEpisode(EpisodeCreateRequest episodeCreateRequest);

  /**
   * Cập nhật thông tin của đối tượng {@link Episode} từ {@link EpisodeUpdateRequest}.
   * <p>
   * Phương thức này sử dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác
   * null.
   * </p>
   *
   * @param episode              đối tượng {@code Episode} cần cập nhật
   * @param episodeUpdateRequest đối tượng chứa thông tin cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_VIDEO_URL, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  void updateEpisode(@MappingTarget Episode episode, EpisodeUpdateRequest episodeUpdateRequest);

  // ----------------- Các phương thức lọc thông tin -----------------

  /**
   * Lọc thông tin của {@link EpisodeResponse} khi hiển thị cho người dùng.
   * <p>
   * Các trường liên quan đến duyệt sẽ bị ẩn đi.
   * </p>
   *
   * @param dto đối tượng {@code EpisodeResponse} cần lọc thông tin
   */
  @Override
  default void filterForUser(EpisodeResponse dto) {
    // Ẩn các trường duyệt
    BaseRoleFilterMapper.super.filterForUser(dto);
    dto.getEpisodePublishingSchedule().setId(null);
    dto.getEpisodePublishingSchedule().setModDeadline(null);
    dto.getEpisodePublishingSchedule().setStatusDeadline(null);
    dto.getEpisodePublishingSchedule().setCreatedAt(null);
    dto.getEpisodePublishingSchedule().setUpdatedAt(null);
    dto.setSubmittedBy(null);
    dto.setReviewedBy(null);
    dto.setSubmittedAt(null);
    dto.setReviewedAt(null);
    dto.setCreatedAt(null);
    dto.setUpdatedAt(null);
    dto.setRejectedReason(null);
  }

  /**
   * Lọc thông tin của {@link EpisodeInAnimeResponse} khi hiển thị cho người dùng.
   *
   * @param dto đối tượng {@code EpisodeInAnimeResponse} cần lọc thông tin
   */
  default void filterForUserEpisodeInAnimeResponse(EpisodeInAnimeResponse dto) {
    dto.setSubmittedBy(null);
    dto.setReviewedBy(null);
    dto.setSubmittedAt(null);
    dto.setReviewedAt(null);
    dto.setStatus(null);
    dto.setRejectedReason(null);
  }

  /**
   * Lọc thông tin của {@link EpisodeResponse} khi hiển thị cho moderator.
   *
   * @param dto đối tượng {@code EpisodeResponse} cần lọc thông tin
   */
  @Override
  default void filterForModerator(EpisodeResponse dto) {
    BaseRoleFilterMapper.super.filterForModerator(dto);
  }
}
