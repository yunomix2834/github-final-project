package com.source.animeh.constant.security;

public class PredefinedEndpoint {

  public static final String FRONTEND_ENDPOINT = "http://192.168.1.30:4200";
  public static final String FRONTEND_ENDPOINT2 = "http://localhost:4200";
  public static final String FRONTEND_ENDPOINT3 = "http://26.229.106.182:4200";
  public static final String FRONTEND_ENDPOINT4 = "http://127.0.0.1:5500";
  public static final String[] PUBLIC_ENDPOINTS = {
      "/auth/**",
      "/moderator-register/guest",
      "/hls/**",
      "/media/**",
      "/public/**",
      "/animes-query/**",
      "/profile-image/**",
      "/ws/*"
  };

  private PredefinedEndpoint() {
  }
}
