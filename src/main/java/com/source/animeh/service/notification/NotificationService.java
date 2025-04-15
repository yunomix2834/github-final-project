package com.source.animeh.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.source.animeh.data.NotificationData;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.notification.Notification;
import com.source.animeh.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  public NotificationService(NotificationRepository notificationRepository,
      ObjectMapper objectMapper) {
    this.notificationRepository = notificationRepository;
    this.objectMapper = objectMapper;
  }

  public void createNotification(
      User sender,
      User receiver,
      NotificationData notificationData) {
    try {
      // Map NotificationData -> jsonString
      String jsonData = objectMapper.writeValueAsString(notificationData);
      Notification notification = new Notification();
      notification.setId(UUID.randomUUID().toString());
      notification.setSender(sender);
      notification.setReceiver(receiver);
      notification.setJsonData(jsonData);
      notification.setCreatedAt(LocalDateTime.now());

      notificationRepository.save(notification);

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
