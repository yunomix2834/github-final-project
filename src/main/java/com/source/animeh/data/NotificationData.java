package com.source.animeh.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class NotificationData {

  String title;
  String message;
  String time;
  String type;
  String targetUrl;
  String targetType;
  String targetReference;
  Boolean isRead;
  String readAt;
}
