package com.source.animeh.configuration.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.source.animeh.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lớp filter xác thực JWT, kiểm tra và thiết lập thông tin người dùng vào SecurityContext.
 * <p>
 * Lớp này sẽ lấy token từ header Authorization (theo chuẩn "Bearer "), sau đó xác thực token, lấy
 * thông tin người dùng và thiết lập thông tin vào SecurityContextHolder.
 * </p>
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // ===================== Các hằng số cấu hình =====================
  private static final String HEADER_AUTHORIZATION = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
  JwtUtils jwtUtils;
  CustomUserDetailsService userDetailsService;

  /**
   * Phương thức lọc xác thực cho mỗi request.
   * <p>
   * Lấy token từ header Authorization, xác thực token và nếu token hợp lệ sẽ thiết lập thông tin
   * người dùng vào SecurityContextHolder.
   * </p>
   *
   * @param request     yêu cầu HTTP
   * @param response    phản hồi HTTP
   * @param filterChain chuỗi filter tiếp theo
   * @throws ServletException nếu có lỗi servlet xảy ra
   * @throws IOException      nếu có lỗi nhập/xuất xảy ra
   */
  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain
  ) throws ServletException, IOException {

    // Lấy token từ header Authorization theo chuẩn Bearer
    String token = parseTokenFromHeader(request);

    if (token != null) {
      JWTClaimsSet claims = jwtUtils.validateTokenAndGetClaims(token);
      if (claims != null) {
        // Lấy username từ claim của token
        String username = claims.getSubject();

        // Tải thông tin người dùng từ username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Tạo Authentication và thiết lập vào SecurityContextHolder
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Lấy token JWT từ header Authorization của request.
   * <p>
   * Phương thức này kiểm tra xem header Authorization có chứa token với tiền tố "Bearer " hay
   * không. Nếu có, cắt bỏ tiền tố và trả về phần token còn lại.
   * </p>
   *
   * @param request yêu cầu HTTP
   * @return token JWT nếu tồn tại, ngược lại trả về null
   */
  String parseTokenFromHeader(HttpServletRequest request) {
    String headerAuth = request.getHeader(HEADER_AUTHORIZATION);
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
      return headerAuth.substring(BEARER_PREFIX_LENGTH);
    }
    return null;
  }
}
