package com.source.animeh.configuration.event.anime;

import com.source.animeh.configuration.observer.anime.AnimeObserver;
import java.util.ArrayList;
import java.util.List;

public class AnimeEventSubject {

  private final List<AnimeObserver> observers = new ArrayList<>();

  // Đăng ký Observer
  public void addObserver(AnimeObserver observer) {
    observers.add(observer);
  }

  // Gỡ bỏ Observer
  public void removeObserver(AnimeObserver observer) {
    observers.remove(observer);
  }

  // Gửi thông báo đến toàn bộ Observer
  public void notifyAllObservers(AnimeEvent event) {
    for (AnimeObserver obs : observers) {
      obs.onNotify(event);
    }
  }
}
