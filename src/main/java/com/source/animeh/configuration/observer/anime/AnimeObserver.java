package com.source.animeh.configuration.observer.anime;

import com.source.animeh.configuration.event.anime.AnimeEvent;

public interface AnimeObserver {

  void onNotify(AnimeEvent event);
}