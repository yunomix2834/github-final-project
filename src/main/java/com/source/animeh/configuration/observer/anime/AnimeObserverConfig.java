package com.source.animeh.configuration.observer.anime;

import com.source.animeh.configuration.event.anime.AnimeEventSubject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnimeObserverConfig {

  @Bean
  public AnimeEventSubject animeEventSubject(
      AnimeNotificationObserver animeNotificationObserver) {

    AnimeEventSubject animeEventSubject = new AnimeEventSubject();

    // Đăng ký observer
    animeEventSubject.addObserver(animeNotificationObserver);
    return animeEventSubject;
  }
}
