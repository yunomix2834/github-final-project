package com.source.animeh.controller.user_interaction;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller tương tác người dùng với lượt xem Anime.
 * <p>
 * Controller này sẽ xử lý các thao tác liên quan đến lượt xem của Anime, ví dụ: ghi nhận lượt xem
 * khi người dùng truy cập vào trang chi tiết Anime. Các endpoint được định nghĩa dưới dạng hằng số
 * để dễ bảo trì và thay đổi khi cần.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(AnimeViewController.BASE_ENDPOINT)
public class AnimeViewController {

  // --- Base Endpoint ---
  public static final String BASE_ENDPOINT = "/public/anime-views";

}
