package com.source.animeh.dto.response.schedule.anime;

import com.source.animeh.dto.response.film_series.anime.AnimeSeriesInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.EpisodeInAnimeResponse;
import com.source.animeh.dto.response.film_series.anime.TypeItemResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnimeInPublishingScheduleResponse {

  String id;
  String title;
  String description;
  Integer releaseYear;
  String director;
  Integer expectedEpisodes;
  String posterUrl;
  String bannerUrl;
  String trailerUrl;

  LocalDateTime nextEpisodePublishingDate;

  Integer ratingCount;  // số người đã rating
  Long followCount;     // số người trong watchlist

  // Xếp hạng / lượt view
  BigDecimal averageRating;
  Long viewCount;

  String seriesOrder;

  // Tổng số tập
  Integer totalEpisodeCount;

  // Danh sách Episode con
  List<EpisodeInAnimeResponse> episodes;

  // Danh sách Type
  List<TypeItemResponse> typeItems;

  // Series rút gọn
  AnimeSeriesInAnimeResponse series;

  // Xem có ở trong Watchlist của user hay không
  boolean inWatchlist;

  // Thông tin duyệt
  String statusSubmitted;     // ẩn với user
  String rejectedReason;      // ẩn với user
  String submittedBy;         // ẩn với user
  String reviewedBy;          // ẩn với user
  LocalDateTime submittedAt;  // ẩn với user
  LocalDateTime reviewedAt;   // ẩn với user

  // Thông tin cho Moderator/Admin
  LocalDateTime createdAt; // Moderator
  LocalDateTime updatedAt; // Moderator
}
