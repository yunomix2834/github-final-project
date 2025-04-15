package com.source.animeh.service.user_interaction;

import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_PENDING;
import static com.source.animeh.constant.PredefinedStatus.EPISODE_REQUEST_REJECTED;

import com.source.animeh.data.LocalHistoryData;
import com.source.animeh.dto.request.user_interaction.LocalHistoryRequest;
import com.source.animeh.dto.response.user_interaction.EpisodeInHistory;
import com.source.animeh.dto.response.user_interaction.ViewingHistoryResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.episode.Episode;
import com.source.animeh.entity.user_interaction.ViewingHistory;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.mapper.user_interaction.ViewingHistoryMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.episode.EpisodeRepository;
import com.source.animeh.repository.user_interaction.ViewingHistoryRepository;
import com.source.animeh.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
public class ViewingHistoryService {

  ViewingHistoryRepository viewingHistoryRepository;
  EpisodeRepository episodeRepository;
  UserRepository userRepository;
  ViewingHistoryMapper viewingHistoryMapper;

  /**
   * Ghi nhận người dùng (đã login) đã xem 1 tập, kèm thời gian xem (watchedDate), watchedDuration
   */
  public void addViewHistory(
      String episodeId,
      BigDecimal watchedDuration) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Episode ep = episodeRepository
        .findById(episodeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EPISODE_NOT_FOUND)
        );

    // Kiểm tra status
    if (EPISODE_REQUEST_PENDING.getStatus().equalsIgnoreCase(ep.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_PENDING);
    }

    if (EPISODE_REQUEST_REJECTED.getStatus().equalsIgnoreCase(ep.getStatus())) {
      throw new AppException(ErrorCode.INVALID_STATUS_REJECTED);
    }

//    // Tìm tất cả record cũ (userId + episodeId)
//    List<ViewingHistory> duplicates =
//        viewingHistoryRepository
//            .findAllByUserIdAndEpisodeId(user.getId(), episodeId)
//            .stream()
//            .filter(vh -> Boolean.FALSE.equals(vh.getIsDeleted()))
//            .toList();

    // Tìm record (userId + episodeId + isDeleted=false)
    Optional<ViewingHistory> opt = viewingHistoryRepository
        .findByUserIdAndEpisodeIdAndIsDeletedFalse(user.getId(), ep.getId());

    if (opt.isEmpty()) {
      // Chưa có => Tạo mới
      ViewingHistory vh = new ViewingHistory();
      vh.setId(UUID.randomUUID().toString());
      vh.setUser(user);
      vh.setEpisode(ep);
      vh.setWatchedDuration(watchedDuration != null
          ? watchedDuration
          : BigDecimal.ZERO);
      vh.setWatchedDate(LocalDateTime.now());
      vh.setIsDeleted(false);
      vh.setCreatedAt(LocalDateTime.now());
      viewingHistoryRepository.save(vh);

    } else {
      ViewingHistory existing = opt.get();

      LocalDateTime now = LocalDateTime.now();
      if (now.isAfter(existing.getWatchedDate())) {
        existing.setWatchedDate(now);
      }

      // Merge watchedDuration:
      if (watchedDuration != null) {
        BigDecimal oldDur = (existing.getWatchedDuration() != null)
            ? existing.getWatchedDuration()
            : BigDecimal.ZERO;
        // Lấy giá trị lớn hơn
        if (watchedDuration.compareTo(oldDur) > 0) {
          existing.setWatchedDuration(watchedDuration);
        }
      }

      existing.setUpdatedAt(LocalDateTime.now());
      existing.setIsDeleted(false);
      viewingHistoryRepository.save(existing);
    }
  }

  /**
   * Lấy toàn bộ lịch sử xem
   */
  public Page<ViewingHistoryResponse> getAllHistory(
      int page, int size) {

    User user = SecurityUtils.getCurrentUser(userRepository);

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    return viewingHistoryRepository
        .findApprovedByUserId(user.getId(), pageable)
        .map(viewingHistoryMapper::toViewingHistoryResponse);
  }

  // Xoá lịch sử xem phim bằng cách deactive
  public void deleteViewHistory(String id) {
    ViewingHistory vh = viewingHistoryRepository
        .findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.HISTORY_NOT_FOUND)
        );
    vh.setIsDeleted(true);
    vh.setUpdatedAt(LocalDateTime.now());
    viewingHistoryRepository.save(vh);
  }

  /**
   * Trả về 1 Page<ViewingHistoryResponse> từ localData, không ghi vào DB.
   */
  public Page<ViewingHistoryResponse> getLocalViewHistory(
      LocalHistoryRequest request,
      int page, int size) {
    if (request == null || request.getLocalData() == null) {
      // Nếu không có data => trả về page rỗng
      return Page.empty();
    }

    // Duyệt localData => chuyển thành list<ViewingHistoryResponse>
    List<ViewingHistoryResponse> allItems = new ArrayList<>();
    for (LocalHistoryData item : request.getLocalData()) {

      // Tìm episode
      Episode ep = episodeRepository.findById(item.getEpisodeId()).orElse(null);

      // Tạo object tạm => mapping sang ViewingHistoryResponse
      ViewingHistoryResponse tempResp = new ViewingHistoryResponse();
      tempResp.setId(UUID.randomUUID().toString()); // ID tạm
      tempResp.setWatchedDate(item.getWatchedDate());
      tempResp.setWatchedDuration(item.getWatchedDuration());

      if (ep != null) {
        // Giả lập 1 ViewingHistory tạm => mapper => response
        ViewingHistory fakeVH = ViewingHistory.builder()
            .id(tempResp.getId())
            .episode(ep)
            .watchedDate(item.getWatchedDate())
            .watchedDuration(item.getWatchedDuration())
            .build();

        ViewingHistoryResponse mapped =
            viewingHistoryMapper.toViewingHistoryResponse(fakeVH);
        // Gộp các trường cũ => override
        mapped.setId(tempResp.getId());
        mapped.setWatchedDate(tempResp.getWatchedDate());
        mapped.setWatchedDuration(tempResp.getWatchedDuration());

        tempResp = mapped;
      } else {
        // Episode không tồn tại => build EpisodeInHistory "ảo"
        EpisodeInHistory eih = new EpisodeInHistory();
        eih.setId(item.getEpisodeId());
        tempResp.setEpisode(eih);
      }

      allItems.add(tempResp);
    }

    // Phân trang thủ công (in-memory)
    //    Tạo Pageable (page, size) => subList => PageImpl
    int total = allItems.size();
    int start = page * size;
    int end = Math.min(start + size, total);

    if (start >= total) {
      // Page rỗng
      List<ViewingHistoryResponse> emptyList = Collections.emptyList();
      return new PageImpl<>(emptyList, PageRequest.of(page, size), total);
    }

    List<ViewingHistoryResponse> pageContent = allItems.subList(start, end);

    // Trả về Page<ViewingHistoryResponse>
    return new PageImpl<>(pageContent, PageRequest.of(page, size), total);
  }


  /**
   * Đồng bộ local => DB, localData: danh sách {episodeId, watchedDate, watchedDuration}
   */
  @Transactional
  public void syncLocalHistory(
      LocalHistoryRequest localHistoryRequest) {
    User user = SecurityUtils.getCurrentUser(userRepository);

    List<LocalHistoryData> localData = localHistoryRequest.getLocalData();

    for (LocalHistoryData item : localData) {
      Episode ep = episodeRepository.findById(item.getEpisodeId())
          .orElse(null);

      // Bỏ qua case k tìm thấy episode
      if (ep == null) {
        continue;
      }

      // Tìm xem user đã có record cho episode này chưa
      Optional<ViewingHistory> opt = viewingHistoryRepository
          .findByUserIdAndEpisodeId(
              user.getId(), ep.getId()
          );
      if (opt.isPresent()) {
        // Merge
        ViewingHistory existing = opt.get();

        // update watchedDate => nếu local item "mới" hơn
        if (item.getWatchedDate().isAfter(existing.getWatchedDate())) {
          existing.setWatchedDate(item.getWatchedDate());
        }

        // update watchedDuration => nếu local item lớn hơn
        if (item.getWatchedDuration() != null) {
          BigDecimal oldDur = (existing.getWatchedDuration() != null)
              ? existing.getWatchedDuration()
              : BigDecimal.ZERO;

          if (item.getWatchedDuration().compareTo(oldDur) > 0) {
            existing.setWatchedDuration(item.getWatchedDuration());
          }
        }
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setIsDeleted(false);
        viewingHistoryRepository.save(existing);
      } else {
        ViewingHistory viewingHistory = new ViewingHistory();
        viewingHistory.setId(UUID.randomUUID().toString());
        viewingHistory.setUser(user);
        viewingHistory.setEpisode(ep);
        viewingHistory.setWatchedDate(item.getWatchedDate() != null
            ? item.getWatchedDate()
            : LocalDateTime.now());
        viewingHistory.setWatchedDuration(item.getWatchedDuration());
        viewingHistory.setIsDeleted(false);
        viewingHistory.setCreatedAt(LocalDateTime.now());
        viewingHistoryRepository.save(viewingHistory);
      }
    }
  }
}
