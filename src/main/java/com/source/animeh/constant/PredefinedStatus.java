package com.source.animeh.constant;

import lombok.Getter;

@Getter
public enum PredefinedStatus {

  MODERATOR_REQUEST_APPROVED("Moderator Request", "approved"),
  MODERATOR_REQUEST_PENDING("Moderator Request", "pending"),
  MODERATOR_REQUEST_REJECTED("Moderator Request", "rejected"),
  FILM_REQUEST_APPROVED("Film Request", "approved"),
  FILM_REQUEST_PENDING("Film Request", "pending"),
  FILM_REQUEST_REJECTED("Film Request", "rejected"),
  EPISODE_REQUEST_APPROVED("Episode Request", "approved"),
  EPISODE_REQUEST_PENDING("Episode Request", "pending"),
  EPISODE_REQUEST_REJECTED("Episode Request", "rejected"),
  STATUS_CREATE("Status", "create"),
  STATUS_UPDATE("Status", "update"),
  STATUS_DELETE("Status", "delete"),
  STATUS_APPROVED("Status", "approved"),
  STATUS_PENDING("Status", "pending"),
  STATUS_REJECTED("Status", "rejected"),
  ;

  private final String name;
  private final String status;

  PredefinedStatus(String name, String status) {
    this.name = name;
    this.status = status;
  }
}
