package com.source.animeh.configuration.observer.anime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.source.animeh.configuration.event.anime.AnimeEvent;
import com.source.animeh.data.NotificationData;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.service.notification.NotificationService;
import java.time.LocalDateTime;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AnimeNotificationObserver implements AnimeObserver {

  // Service để lưu Notification DB
  private final NotificationService notificationService;

  // Để bắn WebSocket
  private final SimpMessagingTemplate simpMessagingTemplate;

  private final ObjectMapper objectMapper;

  public AnimeNotificationObserver(
      NotificationService notificationService,
      SimpMessagingTemplate simpMessagingTemplate,
      ObjectMapper objectMapper
  ) {
    this.notificationService = notificationService;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onNotify(AnimeEvent event) {

    String eventType = event.getEventType();
    Anime anime = event.getAnime();
    User actor = event.getActor();

    // Xây dựng JSON data
    NotificationData data = new NotificationData();
    data.setTitle("Anime " + eventType);
    data.setMessage("Bộ anime " + anime.getTitle() + " đã xảy ra event " + eventType);
    data.setTime(LocalDateTime.now().toString());
    data.setType(eventType);
    data.setTargetUrl("/anime/" + anime.getId());
    data.setTargetType("ANIME");
    data.setTargetReference(anime.getId());
    data.setIsRead(false);
    data.setReadAt(null);

    // Tạo notification -> Lưu db
    notificationService.createNotification(
        actor,
        /* receiver? */ null,
        data);

    // Đẩy WebSocket => "/topic/notification"
    simpMessagingTemplate.convertAndSend("/topic/notification", data);
  }
}
