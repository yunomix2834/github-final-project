package com.source.animeh.interface_package.service.film_series;

import java.io.IOException;

public interface VideoLibraryServiceInterface {

  // Tạo folder: unapproved/<seriesId>/<animeId>
  void createAnimeFolder(
      String storeType,
      String seriesId,
      String animeId);

  /**
   * Transcode 1 episode => tạo folder episode Output:
   * LIBRARY_ROOT/storeType/<seriesId>/<animeId>/<episodeId>/
   */
  void transcodeEpisodeHls(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId,
      String inputFile)
      throws IOException, InterruptedException;

  /**
   * Trả về URL => /hls/{storeType}/{seriesId}/{animeId}/{episodeId}/master.m3u8
   */
  String getEpisodeMasterUrl(
      String storeType,
      String seriesId,
      String animeId,
      String episodeId);

  // Di chuyển Anime folder => unapproved -> approved
  void moveAnimeFolder(
      String fromStore,
      String toStore,
      String seriesId,
      String animeId);

  // Di chuyển Episode folder => unapproved -> approved
  void moveEpisodeFolder(
      String fromStore,
      String toStore,
      String seriesId,
      String animeId,
      String episodeId);

  // Xoá folder  => unapproved/<seriesId>/<animeId>/(<episodeId>)
  // TODO trả ra mã lỗi trong ErrorCode
  void deleteFolder(
      String storeType,
      String... pathElems)
      throws IOException;
}
