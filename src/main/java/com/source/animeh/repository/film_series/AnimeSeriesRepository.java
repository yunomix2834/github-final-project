package com.source.animeh.repository.film_series;

import com.source.animeh.entity.film_series.AnimeSeries;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository quản lý đối tượng {@link AnimeSeries} dùng cho chức năng quản lý series phim.
 */
public interface AnimeSeriesRepository extends JpaRepository<AnimeSeries, String> {

}
