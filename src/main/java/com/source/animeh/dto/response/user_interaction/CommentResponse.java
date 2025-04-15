package com.source.animeh.dto.response.user_interaction;

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
public class CommentResponse {

  String id;
  String parentId;
  String content;
  Boolean isDeactivated;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  UserInCommentResponse user;

  List<CommentResponse> replies;
}
