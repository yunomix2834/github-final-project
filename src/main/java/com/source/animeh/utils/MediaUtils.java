package com.source.animeh.utils;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_REJECTED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_APPROVED;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_REJECTED;
import static com.source.animeh.constant.account.PredefinedRole.ADMIN;
import static com.source.animeh.constant.account.PredefinedRole.MOD;
import static com.source.animeh.constant.media.PredefinedMedia.DOT_GIF;
import static com.source.animeh.constant.media.PredefinedMedia.DOT_JPEG;
import static com.source.animeh.constant.media.PredefinedMedia.DOT_PNG;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.film_series.Anime;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;

/**
 * Lớp tiện ích cho các thao tác liên quan đến media, bao gồm phát hiện kiểu media và lọc dữ liệu.
 */
public class MediaUtils {

  /**
   * Phát hiện kiểu media của một file dựa trên đuôi file.
   *
   * @param path Đường dẫn đến file cần phát hiện
   * @return Kiểu media của file hoặc APPLICATION_OCTET_STREAM nếu không nhận diện được
   */
  public static MediaType detectMediaType(Path path) {
    String fileName = path.getFileName().toString().toLowerCase();

    if (fileName.endsWith(DOT_PNG)) {
      return MediaType.IMAGE_PNG;
    } else if (fileName.endsWith(DOT_JPEG)) {
      return MediaType.IMAGE_JPEG;
    } else if (fileName.endsWith(DOT_GIF)) {
      return MediaType.IMAGE_GIF;
    }
    return MediaType.APPLICATION_OCTET_STREAM;
  }

  /**
   * Lọc các tập phim dựa trên vai trò của người dùng.
   *
   * @param allEpisodes Danh sách tập phim để lọc
   * @param user        Người dùng đang tương tác với hệ thống
   * @return Danh sách các tập phim đã được lọc
   */
  public static List<Episode> filterEpisodesByRole(List<Episode> allEpisodes, User user) {
    if (user == null || user.getRole() == null) {
      return allEpisodes.stream()
          .filter(ep -> EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(ep.getStatus()))
          .collect(Collectors.toList());
    }

    String roleName = user.getRole().getName().toUpperCase();
    return switch (roleName) {
      case ADMIN -> allEpisodes;
      case MOD -> allEpisodes.stream()
          .filter(e -> EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(e.getStatus()) ||
              (e.getSubmittedBy() != null && e.getSubmittedBy().getId().equals(user.getId()) &&
                  (EPISODE_REQUEST_PENDING.getStatus().equalsIgnoreCase(e.getStatus()) ||
                      EPISODE_REQUEST_REJECTED.getStatus().equalsIgnoreCase(e.getStatus()))))
          .collect(Collectors.toList());
      default -> allEpisodes.stream()
          .filter(ep -> EPISODE_REQUEST_APPROVED.getStatus().equalsIgnoreCase(ep.getStatus()))
          .collect(Collectors.toList());
    };
  }

  /**
   * Lọc các bộ anime dựa trên vai trò của người dùng.
   *
   * @param animes      Danh sách các bộ anime để lọc
   * @param currentUser Người dùng hiện tại
   * @return Danh sách các bộ anime đã được lọc
   */
  public static List<Anime> filterAnimesByRole(List<Anime> animes, User currentUser) {
    if (currentUser == null) {
      return animes.stream()
          .filter(a -> FILM_REQUEST_APPROVED.getStatus().equals(a.getStatusSubmitted()))
          .collect(Collectors.toList());
    }

    String role = currentUser.getRole().getName();
    return switch (role) {
      case ADMIN -> animes;
      case MOD -> animes.stream()
          .filter(a -> FILM_REQUEST_APPROVED.getStatus().equalsIgnoreCase(a.getStatusSubmitted()) ||
              (a.getSubmittedBy() != null && a.getSubmittedBy().getId().equals(currentUser.getId())
                  &&
                  (FILM_REQUEST_PENDING.getStatus().equalsIgnoreCase(a.getStatusSubmitted()) ||
                      FILM_REQUEST_REJECTED.getStatus().equalsIgnoreCase(a.getStatusSubmitted()))))
          .collect(Collectors.toList());
      default -> animes.stream()
          .filter(a -> FILM_REQUEST_APPROVED.getStatus().equals(a.getStatusSubmitted()))
          .collect(Collectors.toList());
    };
  }

  /**
   * Lọc và sắp xếp các tập phim dựa trên vai trò của người dùng và số tập.
   *
   * @param episodes    Danh sách tập phim
   * @param currentUser Người dùng hiện tại
   * @return Danh sách tập phim đã được lọc và sắp xếp
   */
  public static List<Episode> filterAndSortEpisodesByRole(List<Episode> episodes,
      User currentUser) {
    List<Episode> filtered = filterEpisodesByRole(episodes, currentUser);
    return filtered.stream()
        .sorted(Comparator.comparing(Episode::getEpisodeNumber))
        .collect(Collectors.toList());
  }
}
