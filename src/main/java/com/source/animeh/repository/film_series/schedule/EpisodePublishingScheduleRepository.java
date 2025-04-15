package com.source.animeh.repository.film_series.schedule;

import com.source.animeh.entity.film_series.schedule.EpisodePublishingSchedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EpisodePublishingScheduleRepository
    extends JpaRepository<EpisodePublishingSchedule, String>,
    JpaSpecificationExecutor<EpisodePublishingSchedule> {

  Optional<EpisodePublishingSchedule> findByEpisodeId(String episodeId);

  Boolean existsByEpisodeId(String episodeId);

  void deleteByEpisodeId(String episodeId);
}
