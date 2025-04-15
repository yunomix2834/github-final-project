package com.source.animeh.interface_package.service.film_series.moderator;

public interface AdminFilmServiceInterface {

  /**
   * Admin duyệt Anime => Move folder unapproved -> approved => set status_submitted=APPROVED
   */
  void approveAnime(
      String animeId);

  /**
   * Admin từ chối Anime => set status_submitted=REJECTED, reason
   */
  void rejectAnime(
      String animeId,
      String reason);

  /**
   * Admin duyệt tập phim => move folder unapproved -> approved => set status=APPROVED
   */
  void approveEpisode(
      String episodeId);

  /**
   * Admin từ chối Episode => set status_submitted=REJECTED, reason
   */
  void rejectEpisode(
      String episodeId,
      String reason);
}
