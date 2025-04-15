package com.source.animeh.mapper.film_series.schedule;

import com.source.animeh.dto.request.film_series.anime.schedule.EpisodePublishingScheduleUpdateRequest;
import com.source.animeh.dto.response.schedule.episode.EpisodePublishingScheduleResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import com.source.animeh.mapper.episode.EpisodeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {EpisodeMapper.class})
public interface EpisodePublishingScheduleMapper {

  @Mapping(target = "episode",
      source = "entity.episode",
      qualifiedByName = "mapEpisodePublishResponse")
  EpisodePublishingScheduleResponse toEpisodeScheduleResponse(EpisodePublishingSchedule entity,
      @Context User currentUser);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "scheduleType", ignore = true)
  void updateEpisodeSchedule(@MappingTarget EpisodePublishingSchedule entity,
      EpisodePublishingScheduleUpdateRequest updateRequest);
}
