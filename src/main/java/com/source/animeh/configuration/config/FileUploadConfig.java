package com.source.animeh.configuration.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

/**
 * Cấu hình upload file cho ứng dụng.
 * <p>
 * Cấu hình này giới hạn kích thước file upload và request lên tối đa 100MB.
 * </p>
 */
@Configuration
public class FileUploadConfig {

  // Kích thước tối đa cho file và request (tính theo MB)
  private static final int MAX_SIZE_MB = 100;

  /**
   * Tạo bean cấu hình upload file.
   *
   * @return đối tượng {@link MultipartConfigElement} chứa cấu hình upload file
   */
  @Bean
  public MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofMegabytes(MAX_SIZE_MB));
    factory.setMaxRequestSize(DataSize.ofMegabytes(MAX_SIZE_MB));
    return factory.createMultipartConfig();
  }
}
