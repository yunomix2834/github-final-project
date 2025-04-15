package com.source.animeh.repository.episode;

import com.source.animeh.entity.episode.EpisodeModerationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link EpisodeModerationHistory} dùng cho chức năng lưu trữ lịch sử
 * duyệt (moderation) của các episode.
 */
public interface EpisodeModerationHistoryRepository extends
    JpaRepository<EpisodeModerationHistory, String> {

}
