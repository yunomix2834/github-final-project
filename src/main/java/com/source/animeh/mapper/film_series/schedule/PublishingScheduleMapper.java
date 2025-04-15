package com.source.animeh.mapper.film_series.schedule;

import com.source.animeh.dto.request.film_series.anime.schedule.PublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.schedule.anime.PublishingScheduleResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.schedule.PublishingSchedule;
import com.source.animeh.mapper.film_series.AnimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper chuyển đổi giữa đối tượng {@link PublishingSchedule} và các DTO liên quan.
 * <p>
 * Mapper này chuyển đổi từ entity {@code PublishingSchedule} sang
 * {@code PublishingScheduleResponse} và cập nhật thông tin của {@code PublishingSchedule} từ
 * {@code PublishingScheduleUpdateRequest}.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {AnimeMapper.class})
public interface PublishingScheduleMapper {


  // Hằng số định nghĩa các field mapping
  String FIELD_ID = "id";
  String FIELD_ANIME = "anime";
  String FIELD_CREATED_AT = "createdAt";
  String FIELD_UPDATED_AT = "updatedAt";

  /**
   * Chuyển đổi đối tượng {@link PublishingSchedule} sang {@link PublishingScheduleResponse}.
   *
   * @param entity đối tượng {@code PublishingSchedule} cần chuyển đổi
   * @return đối tượng {@code PublishingScheduleResponse} chứa thông tin lịch phát hành
   */
  @Mapping(target = "anime",
      source = "entity.anime",
      qualifiedByName = "mapAnimePublishResponse")
  PublishingScheduleResponse toScheduleResponse(PublishingSchedule entity,
      @Context User currentUser);

  /**
   * Cập nhật thông tin của đối tượng {@link PublishingSchedule} từ
   * {@link PublishingScheduleUpdateRequest}.
   * <p>
   * Phương thức này sử dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác
   * null. Các trường sau được bỏ qua: {@code id}, {@code anime}, {@code createdAt} và
   * {@code updatedAt}.
   * </p>
   *
   * @param entity        đối tượng {@code PublishingSchedule} cần cập nhật
   * @param updateRequest đối tượng chứa thông tin cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = FIELD_ANIME, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  @Mapping(target = "scheduleType", ignore = true)
  void updateSchedule(@MappingTarget PublishingSchedule entity,
      PublishingScheduleUpdateRequest updateRequest);

}
