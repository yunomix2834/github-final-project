package com.source.animeh.interface_package.service.file;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageServiceInterface {

  // Lưu poster/banner cho Series
  // type = "poster" hoặc "banner"
  String storeSeriesMedia(
      MultipartFile file,
      String seriesId,
      String type)
      throws IOException;

  // Lưu poster/banner cho Anime
  // type = "poster" hoặc "banner"
  String storeAnimeMedia(
      MultipartFile file,
      String animeId,
      String type)
      throws IOException;

}
