package com.source.animeh.configuration.config;

import static com.source.animeh.constant.security.PredefinedRoot.HLS_ROOT;
import static com.source.animeh.constant.security.PredefinedRoot.IMAGE_ROOT_FOLDER;
import static com.source.animeh.constant.security.PredefinedRoot.IMAGE_ROOT_PROFILE;
import static com.source.animeh.constant.security.PredefinedRoot.MEDIA_ROOT;
import static com.source.animeh.constant.security.PredefinedRoot.PROFILE_IMAGE_ROOT;
import static com.source.animeh.constant.security.PredefinedRoot.VIDEO_ROOT_FOLDER;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình tài nguyên tĩnh cho ứng dụng.
 * <p>
 * Lớp này đăng ký các resource handler để ánh xạ các request đến các thư mục chứa tài nguyên tĩnh
 * như video HLS, ảnh anime/series và ảnh hồ sơ người dùng. Các giá trị đường dẫn (ví dụ:
 * {@code HLS_ROOT}, {@code VIDEO_ROOT_FOLDER},...) được định nghĩa sẵn trong
 * {@code PredefinedRoot}.
 * </p>
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

  /**
   * Đăng ký các resource handler.
   * <p>
   * Cụ thể:
   * <ul>
   *   <li>Các request khớp với đường dẫn {@code HLS_ROOT} sẽ được chuyển đến thư mục chứa video HLS (VIDEO_ROOT_FOLDER).</li>
   *   <li>Các request khớp với đường dẫn {@code MEDIA_ROOT} sẽ được chuyển đến thư mục chứa ảnh anime/series (IMAGE_ROOT_FOLDER).</li>
   *   <li>Các request khớp với đường dẫn {@code PROFILE_IMAGE_ROOT} sẽ được chuyển đến thư mục chứa ảnh hồ sơ (IMAGE_ROOT_PROFILE).</li>
   * </ul>
   * </p>
   *
   * @param registry đối tượng {@link ResourceHandlerRegistry} dùng để đăng ký các handler
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    // Đăng ký handler cho HLS:
    // Các request khớp với HLS_ROOT sẽ được chuyển đến VIDEO_ROOT_FOLDER.
    registry.addResourceHandler(HLS_ROOT)
        .addResourceLocations(VIDEO_ROOT_FOLDER);

    // Đăng ký handler cho ảnh anime và series:
    // Các request khớp với MEDIA_ROOT sẽ được chuyển đến IMAGE_ROOT_FOLDER.
    registry.addResourceHandler(MEDIA_ROOT)
        .addResourceLocations(IMAGE_ROOT_FOLDER);

    // Đăng ký handler cho ảnh hồ sơ:
    // Các request khớp với PROFILE_IMAGE_ROOT sẽ được chuyển đến IMAGE_ROOT_PROFILE.
    registry.addResourceHandler(PROFILE_IMAGE_ROOT)
        .addResourceLocations(IMAGE_ROOT_PROFILE);
  }
}
