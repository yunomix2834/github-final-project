package com.source.animeh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Lớp chính của ứng dụng Animeh.
 * <p>
 * Đây là class chính của ứng dụng Spring Boot, chịu trách nhiệm khởi chạy ứng dụng.
 * </p>
 */
@SpringBootApplication
@EnableScheduling
public class AnimehApplication {

  /**
   * Phương thức main khởi chạy ứng dụng Animeh.
   *
   * @param args các tham số dòng lệnh khi chạy ứng dụng
   */
  public static void main(String[] args) {
    SpringApplication.run(AnimehApplication.class, args);
  }
}
