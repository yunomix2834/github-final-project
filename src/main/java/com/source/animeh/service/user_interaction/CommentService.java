package com.source.animeh.service.user_interaction;

import com.source.animeh.dto.response.user_interaction.CommentResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.user_interaction.Comment;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.mapper.user_interaction.CommentMapper;
import com.source.animeh.repository.account.UserRepository;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.user_interaction.CommentRepository;
import com.source.animeh.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

  CommentRepository commentRepository;
  AnimeRepository animeRepository;
  UserRepository userRepository;
  CommentMapper commentMapper;

  /**
   * Tạo comment (user đã login), không có parent => comment gốc.
   */
  public CommentResponse createComment(
      String animeId,
      String content) {

    User user = SecurityUtils.getCurrentUser(userRepository);
    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_NOT_FOUND)
        );

    Comment cmt = new Comment();
    cmt.setId(UUID.randomUUID().toString());
    cmt.setAnime(anime);
    cmt.setUser(user);
    cmt.setContent(content);
    cmt.setParent(null);
    cmt.setIsDeactivate(false);
    cmt.setCreatedAt(LocalDateTime.now());
    return commentMapper
        .toCommentResponse(commentRepository.save(cmt));
  }

  /**
   * Reply comment => có parentId
   */
  public CommentResponse replyComment(
      String animeId,
      String parentId,
      String content) {

    User user = SecurityUtils.getCurrentUser(userRepository);
    Anime anime = animeRepository.findById(animeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_NOT_FOUND)
        );

    commentRepository.findById(parentId)
        .orElseThrow(
            () -> new AppException(ErrorCode.COMMENT_NOT_FOUND)
        );

    Comment reply = new Comment();
    reply.setId(UUID.randomUUID().toString());
    reply.setAnime(anime);
    reply.setUser(user);
    reply.setContent(content);
    reply.setParent(parentId);
    reply.setIsDeactivate(false);
    reply.setCreatedAt(LocalDateTime.now());
    return commentMapper
        .toCommentResponse(commentRepository.save(reply));
  }

  public Page<CommentResponse> getActiveRootComments(
      String animeId,
      int page,
      int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    Page<Comment> cmtPage = commentRepository.findAllActiveRootCommentsByAnime(animeId, pageable);

    return cmtPage.map(this::buildCommentTreeResponse);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Page<CommentResponse> getAllRootComments(
      String animeId,
      int page,
      int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    Page<Comment> cmtPage = commentRepository.findAllRootCommentsByAnime(animeId, pageable);

    return cmtPage.map(this::buildCommentTreeResponse);
  }

  /**
   * Lấy tất cả comment (kể cả ẩn)
   */
  @PreAuthorize("hasRole('ADMIN')")
  public Page<CommentResponse> getAllComments(
      String animeId, int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    // Tìm tất cả comment (kể cả ẩn), sắp xếp createdAt desc
    Page<Comment> cmtPage = commentRepository
        .findAllCommentsByAnime(animeId, pageable);

    return cmtPage.map(this::buildCommentTreeResponse);
  }

  /**
   * Lấy tất cả comment (chưa  ẩn)
   */
  public Page<CommentResponse> getActiveComments(
      String animeId, int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    // Tìm tất cả comment (kể cả ẩn), sắp xếp createdAt desc
    Page<Comment> cmtPage = commentRepository
        .findAllActiveCommentsByAnime(animeId, pageable);

    return cmtPage.map(this::buildCommentTreeResponse);
  }

  /**
   * Ẩn comment
   */
  @PreAuthorize("hasRole('ADMIN')")
  public void deactiveComment(String commentId) {
    Comment cmt = commentRepository.findById(commentId)
        .orElseThrow(
            () -> new AppException(ErrorCode.COMMENT_NOT_FOUND)
        );

    cmt.setIsDeactivate(true);
    cmt.setUpdatedAt(LocalDateTime.now());
    commentRepository.save(cmt);
  }

  /**
   * Active lại comment
   */
  @PreAuthorize("hasRole('ADMIN')")
  public void activeComment(String commentId) {
    Comment cmt = commentRepository.findById(commentId)
        .orElseThrow(
            () -> new AppException(ErrorCode.COMMENT_NOT_FOUND)
        );

    cmt.setIsDeactivate(false);
    cmt.setUpdatedAt(LocalDateTime.now());
    commentRepository.save(cmt);
  }


  CommentResponse buildCommentTreeResponse(Comment cmt) {
    CommentResponse res = commentMapper.toCommentResponse(cmt);

    // Tìm List con
    List<Comment> children = commentRepository
        .findReplies(cmt.getId());
    if (!children.isEmpty()) {
      // Mỗi con => buildCommentTreeResponse
      List<CommentResponse> childDtos = children.stream()
          .map(this::buildCommentTreeResponse)
          .toList();
      res.setReplies(childDtos);
    } else {
      res.setReplies(null);
    }

    return res;
  }
}
