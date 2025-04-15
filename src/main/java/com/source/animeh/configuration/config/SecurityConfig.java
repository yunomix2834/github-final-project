package com.source.animeh.configuration.config;

import static com.source.animeh.constant.security.PredefinedEndpoint.FRONTEND_ENDPOINT;
import static com.source.animeh.constant.security.PredefinedEndpoint.FRONTEND_ENDPOINT2;
import static com.source.animeh.constant.security.PredefinedEndpoint.FRONTEND_ENDPOINT3;
import static com.source.animeh.constant.security.PredefinedEndpoint.FRONTEND_ENDPOINT4;
import static com.source.animeh.constant.security.PredefinedEndpoint.PUBLIC_ENDPOINTS;
import static com.source.animeh.constant.security.PredefinedMedthod.DELETE_METHOD;
import static com.source.animeh.constant.security.PredefinedMedthod.GET_METHOD;
import static com.source.animeh.constant.security.PredefinedMedthod.OPTIONS_METHOD;
import static com.source.animeh.constant.security.PredefinedMedthod.PATCH_METHOD;
import static com.source.animeh.constant.security.PredefinedMedthod.POST_METHOD;
import static com.source.animeh.constant.security.PredefinedMedthod.PUT_METHOD;

import com.source.animeh.configuration.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Cấu hình bảo mật cho ứng dụng.
 * <p>
 * Cấu hình này thiết lập các thiết lập bảo mật như CORS, CSRF, session management, authorization
 * requests, và gắn bộ lọc xác thực JWT.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // Hằng số cấu hình CORS
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String URL_PATTERN_ALL = "/**";

  @Autowired
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Cấu hình SecurityFilterChain cho ứng dụng.
   * <p>
   * Cấu hình này thực hiện các bước sau:
   * <ul>
   *   <li>Kích hoạt CORS với cấu hình mặc định.</li>
   *   <li>Tắt CSRF (vì đang sử dụng JWT).</li>
   *   <li>Thiết lập session management dưới dạng stateless.</li>
   *   <li>Cho phép truy cập các endpoint công khai và yêu cầu xác thực cho các endpoint còn lại.</li>
   *   <li>Gắn bộ lọc JWT trước bộ lọc xác thực mặc định.</li>
   *   <li>Sử dụng cấu hình HTTP Basic tạm thời.</li>
   * </ul>
   * </p>
   *
   * @param http đối tượng HttpSecurity để cấu hình bảo mật
   * @return SecurityFilterChain đã được cấu hình
   * @throws Exception nếu có lỗi xảy ra trong quá trình cấu hình
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Kích hoạt CORS với cấu hình mặc định
        .cors(Customizer.withDefaults())

        // Tắt CSRF vì sử dụng JWT
        .csrf(AbstractHttpConfigurer::disable)

        // Cấu hình session theo dạng stateless
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Cho phép các PUBLIC_ENDPOINTS và yêu cầu xác thực cho các request khác
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
          auth.anyRequest().authenticated();
        })

        // Gắn bộ lọc kiểm tra JWT trước bộ lọc UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // Sử dụng HTTP Basic tạm thời
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  /**
   * Cấu hình CORS cho ứng dụng.
   * <p>
   * Phương thức này thiết lập:
   * <ul>
   *   <li>Các origin được phép truy cập (danh sách FRONTEND_ENDPOINT, FRONTEND_ENDPOINT2, FRONTEND_ENDPOINT3).</li>
   *   <li>Các phương thức HTTP được phép (GET, POST, PUT, DELETE, PATCH, OPTIONS).</li>
   *   <li>Cho phép tất cả các header.</li>
   *   <li>Cho phép gửi credentials.</li>
   *   <li>Các header được expose (ví dụ: "Authorization").</li>
   * </ul>
   * Áp dụng cấu hình này cho tất cả các endpoint.
   * </p>
   *
   * @return CorsConfigurationSource chứa cấu hình CORS của ứng dụng
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Cho phép các origin truy cập định nghĩa sẵn
    configuration.setAllowedOrigins(
        List.of(FRONTEND_ENDPOINT, FRONTEND_ENDPOINT2, FRONTEND_ENDPOINT3, FRONTEND_ENDPOINT4));

    // Cho phép các phương thức HTTP được định nghĩa
    configuration.setAllowedMethods(
        List.of(GET_METHOD, POST_METHOD, PUT_METHOD, DELETE_METHOD, PATCH_METHOD, OPTIONS_METHOD));

    // Cho phép tất cả các header
    configuration.setAllowedHeaders(List.of("*"));

    // Cho phép gửi credentials (cookie, header, v.v.)
    configuration.setAllowCredentials(true);

    // Expose header "Authorization"
    configuration.setExposedHeaders(List.of(AUTHORIZATION_HEADER));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // Áp dụng cấu hình cho tất cả các endpoint
    source.registerCorsConfiguration(URL_PATTERN_ALL, configuration);
    return source;
  }
}
