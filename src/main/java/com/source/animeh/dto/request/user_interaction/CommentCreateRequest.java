package com.source.animeh.dto.request.user_interaction;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentCreateRequest {

  String animeId;
  String parentId; //optional, nếu null là comment gốc
  String content;
}
