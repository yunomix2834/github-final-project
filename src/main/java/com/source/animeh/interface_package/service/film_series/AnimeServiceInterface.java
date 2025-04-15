package com.source.animeh.interface_package.service.film_series;

import com.source.animeh.dto.request.filter.AnimeFilterRequest;
import com.source.animeh.dto.response.film_series.anime.AnimeResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AnimeServiceInterface {

  /**
   * Lọc Anime cơ bản theo: - Danh sách typeId (tagIds) - Năm phát hành - Rating tối thiểu -
   * nominated => true/false
   */
  Page<AnimeResponse> filterAnimeAdvanced(
      AnimeFilterRequest animeFilterRequest,
      int page,
      int size
  );

  // Lấy top 4 phim có lượt view cao nhất
  List<AnimeResponse> getTop4Animes();
}
