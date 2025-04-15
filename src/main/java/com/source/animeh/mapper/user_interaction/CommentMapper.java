package com.source.animeh.mapper.user_interaction;

import com.source.animeh.dto.response.user_interaction.CommentResponse;
import com.source.animeh.dto.response.user_interaction.UserInCommentResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.user_interaction.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper chuyển đổi giữa đối tượng {@link Comment} và DTO {@link CommentResponse}.
 * <p>
 * Ngoài ra, mapper còn cung cấp phương thức chuyển đổi từ {@link User} sang
 * {@link UserInCommentResponse} để hiển thị thông tin người dùng trong comment.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

  // ===================== Hằng số mapping =====================
  String TARGET_PARENT_ID = "parentId";
  String SOURCE_PARENT = "parent";

  String TARGET_IS_DEACTIVATED = "isDeactivated";
  String SOURCE_IS_DEACTIVATE = "isDeactivate";

  String TARGET_REPLIES = "replies";

  String TARGET_USER = "user";
  String SOURCE_USER = "user";

  String QUALIFIED_MAP_USER_IN_COMMENT = "mapUserInComment";

  /**
   * Chuyển đổi đối tượng {@link Comment} sang {@link CommentResponse}.
   *
   * @param comment đối tượng {@code Comment} cần chuyển đổi
   * @return đối tượng {@code CommentResponse} chứa thông tin của comment
   */
  @Mapping(target = TARGET_PARENT_ID, source = SOURCE_PARENT)
  @Mapping(target = TARGET_IS_DEACTIVATED, source = SOURCE_IS_DEACTIVATE)
  @Mapping(target = TARGET_REPLIES, ignore = true)
  @Mapping(target = TARGET_USER, source = SOURCE_USER, qualifiedByName = QUALIFIED_MAP_USER_IN_COMMENT)
  CommentResponse toCommentResponse(Comment comment);

  /**
   * Chuyển đổi đối tượng {@link User} sang {@link UserInCommentResponse}.
   *
   * @param user đối tượng {@code User} cần chuyển đổi
   * @return đối tượng {@code UserInCommentResponse} chứa thông tin người dùng trong comment, hoặc
   * {@code null} nếu {@code user} là null
   */
  @Named(QUALIFIED_MAP_USER_IN_COMMENT)
  default UserInCommentResponse mapUserInComment(User user) {
    if (user == null) {
      return null;
    }
    UserInCommentResponse res = new UserInCommentResponse();
    res.setId(user.getId());
    res.setUsername(user.getUsername());
    res.setEmail(user.getEmail());
    res.setRole(user.getRole() != null ? user.getRole().getName() : null);
    res.setDisplayName(user.getDisplayName());
    res.setAvatarUrl(user.getAvatarUrl());
    res.setBackgroundUrl(user.getBackgroundUrl());
    return res;
  }
}
