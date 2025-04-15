package com.source.animeh.constant;

import java.time.LocalDateTime;

public class PredefinedTime {

  public static final long SECOND_MILLISECONDS = 1000;
  public static final long MINUTES_MILLISECONDS = 60_000;
  public static final long HOURS_MILLISECONDS = 60 * 60_000;
  public static final long DAY_MILLISECONDS = 24 * 60 * 60_000;
  public static final long WEEK_MILLISECONDS = 7 * DAY_MILLISECONDS;

  public static final int MINUTES_SECONDS = 60;
  public static final int HOURS_SECONDS = 60 * 60;
  public static final int DAY_SECONDS = 24 * 60 * 60;
  public static final int WEEK_SECONDS = 7 * 24 * 60 * 60;

  // Access token
  public static final long JWT_EXPIRATION_TIME = 3 * DAY_MILLISECONDS;
  public static final int JWT_EXPIRATION_TIME_SECONDS = 3 * DAY_SECONDS;

  public static final LocalDateTime JWT_EXPIRY = LocalDateTime.now().plusDays(3);
  public static final LocalDateTime JWT_REFRESH_EXPIRY = LocalDateTime.now().plusDays(7);

  private PredefinedTime() {
  }
}
