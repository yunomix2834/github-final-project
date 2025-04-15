package com.source.animeh.service.user_interaction;

import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.FILM_REQUEST_REJECTED;

import com.source.animeh.data.LocalWatchlistData;
import com.source.animeh.dto.request.user_interaction.LocalWatchlistRequest;
import com.source.animeh.dto.response.user_interaction.AnimeInWatchlist;
import com.source.animeh.dto.response.user_interaction.WatchlistResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.user_interaction.Watchlist;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.mapper.user_interaction.WatchlistMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.user_interaction.WatchlistRepository;
import com.source.animeh.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchlistService {

  WatchlistRepository watchlistRepository;
  AnimeRepository animeRepository;
  UserRepository userRepository;
  WatchlistMapper watchlistMapper;

  /**
   * Thêm anime vào watchlist (Trường hợp user đã login)
   */
  public void addAnimeToWatchlist(String animeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    // Kiểm tra anime
    Anime anime = animeRepository
        .findById(animeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_NOT_FOUND)
        );

    if (FILM_REQUEST_PENDING.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_PENDING);
    }

    if (FILM_REQUEST_REJECTED.getStatus().equalsIgnoreCase(anime.getStatusSubmitted())) {
      throw new AppException(ErrorCode.INVALID_STATUS_REJECTED);
    }

    // Kiểm tra trùng hay không
    Optional<Watchlist> existing = watchlistRepository
        .findByUserIdAndAnimeId(user.getId(), animeId);
    if (existing.isPresent()) {
      // Đã tồn tại => Không thêm
      return;
    }

    Watchlist w = Watchlist.builder()
        .id(UUID.randomUUID().toString())
        .user(user)
        .anime(anime)
        .dateAdded(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .build();

    watchlistRepository.save(w);
  }

  /**
   * Xóa anime khỏi watchlist
   */
  public void removeAnimeFromWatchlist(String animeId) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Optional<Watchlist> optional = watchlistRepository
        .findByUserIdAndAnimeId(user.getId(), animeId);
    optional.ifPresent(watchlistRepository::delete);
  }

  /**
   * Lấy danh sách watchlist của user
   */
  public Page<WatchlistResponse> getUserWatchlist(
      int page, int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    return watchlistRepository
        .findApprovedWatchlistByUserId(user.getId(), pageable)
        .map(watchlistMapper::toWatchlistResponse);
  }

  /**
   * Trả về Page<WatchlistResponse> dựa trên localAnimeIds,
   */
  public Page<WatchlistResponse> getLocalWatchlist(
      LocalWatchlistRequest request,
      int page,
      int size
  ) {
    if (request == null || request.getLocalWatchlistData() == null) {
      return Page.empty(); // trả về page rỗng nếu không có data
    }

    List<LocalWatchlistData> listData = request.getLocalWatchlistData();
    List<WatchlistResponse> allItems = new ArrayList<>();

    for (LocalWatchlistData item : listData) {
      String animeId = item.getAnimeId();
      LocalDateTime dateAdded = item.getDateAdded();

      // Tìm Anime trong DB
      Anime anime = animeRepository.findById(animeId).orElse(null);

      // Tạo 1 WatchlistResponse tạm
      WatchlistResponse tempResp = new WatchlistResponse();
      // ID "giả lập"
      tempResp.setId(UUID.randomUUID().toString());
      tempResp.setDateAdded(dateAdded != null ? dateAdded : LocalDateTime.now());

      if (anime != null) {
        // Giả lập 1 entity Watchlist để mapper => WatchlistResponse
        Watchlist fakeWatchlist = Watchlist.builder()
            .id(tempResp.getId())
            .anime(anime)
            .dateAdded(tempResp.getDateAdded())
            .build();

        // Dùng mapper sẵn có
        WatchlistResponse mapped = watchlistMapper.toWatchlistResponse(fakeWatchlist);
        // Gắn lại ID và dateAdded 'ảo'
        mapped.setId(tempResp.getId());
        mapped.setDateAdded(tempResp.getDateAdded());

        tempResp = mapped;
      } else {
        // Trường hợp animeId không tồn tại
        AnimeInWatchlist aiw = new AnimeInWatchlist();
        aiw.setId(animeId);
        // tuỳ ý gán title= "not found"...
        tempResp.setAnime(aiw);
      }

      allItems.add(tempResp);
    }

    // Tiến hành phân trang in-memory
    int total = allItems.size();
    int start = page * size;
    int end = Math.min(start + size, total);

    if (start >= total) {
      return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), total);
    }

    List<WatchlistResponse> pageContent = allItems.subList(start, end);
    return new PageImpl<>(pageContent, PageRequest.of(page, size), total);
  }

  /**
   * Đồng bộ watchlist từ localStorage lên server, Merge vào DB (thêm các anime chưa có).
   */
  @Transactional
  public void syncLocalWatchlist(
      LocalWatchlistRequest localWatchlistRequest) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    if (localWatchlistRequest == null
        || localWatchlistRequest.getLocalWatchlistData() == null
        || localWatchlistRequest.getLocalWatchlistData().isEmpty()) {
      return; // Không có gì để sync
    }

    List<Watchlist> existing = watchlistRepository.findByUserId(user.getId());

    Map<String, Watchlist> mapAnimeIdToWatchlist = existing.stream()
        .collect(Collectors.toMap(w -> w.getAnime().getId(), w -> w));

    for (LocalWatchlistData item : localWatchlistRequest.getLocalWatchlistData()) {
      Anime anime = animeRepository.findById(item.getAnimeId()).orElse(null);
      if (anime == null) {
        // Không tìm thấy anime => bỏ qua
        continue;
      }

      LocalDateTime localDate = item.getDateAdded() != null
          ? item.getDateAdded()
          : LocalDateTime.now();

      // Kiểm tra xem user đã có record chưa
      Watchlist existRecord = mapAnimeIdToWatchlist.get(item.getAnimeId());
      if (existRecord != null) {
        // Đã có => merge dateAdded
        if (localDate.isBefore(existRecord.getDateAdded())) {
          existRecord.setDateAdded(localDate);
        }
        existRecord.setUpdatedAt(LocalDateTime.now());
        watchlistRepository.save(existRecord);

      } else {
        // Chưa có => thêm mới
        Watchlist w = Watchlist.builder()
            .id(UUID.randomUUID().toString())
            .user(user)
            .anime(anime)
            .dateAdded(localDate)
            .createdAt(LocalDateTime.now())
            .build();

        watchlistRepository.save(w);
      }
    }
  }
}
