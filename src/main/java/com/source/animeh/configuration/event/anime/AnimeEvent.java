package com.source.animeh.configuration.event.anime;

import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AnimeEvent {

  String eventType;
  Anime anime;
  User actor;


  public AnimeEvent(String eventType, Anime anime, User moderator) {
    this.eventType = eventType;
    this.anime = anime;
    this.actor = moderator;
  }
}
