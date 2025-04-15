package com.source.animeh.dto.response.user_interaction;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInCommentResponse {

  String id;
  String username;
  String email;
  String role;
  String displayName;
  String avatarUrl;
  String backgroundUrl;
}
