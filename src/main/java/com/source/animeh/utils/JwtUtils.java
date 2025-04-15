package com.source.animeh.utils;

import static com.source.animeh.constant.PredefinedTime.JWT_EXPIRATION_TIME;
import static com.source.animeh.constant.security.PredefinedToken.JWT_SECRET;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import java.time.Instant;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Lớp tiện ích để sinh và kiểm tra JWT tokens.
 */
@Component
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class JwtUtils {

  /**
   * Sinh token với thời gian hết hạn mặc định.
   *
   * @param subject Tên người dùng
   * @param role    Vai trò người dùng
   * @return Chuỗi token JWT
   */
  public String generateToken(String subject, String role) {
    return generateToken(subject, role, JWT_EXPIRATION_TIME);
  }

  /**
   * Sinh token JWT.
   *
   * @param subject                Tên người dùng
   * @param role                   Vai trò người dùng
   * @param expirationMilliseconds Thời gian hết hạn token tính bằng milliseconds
   * @return Chuỗi token JWT
   */
  public String generateToken(String subject, String role, long expirationMilliseconds) {
    try {
      Instant now = Instant.now();
      Instant expiry = now.plusMillis(expirationMilliseconds);

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(subject)
          .claim("role", role)
          .issueTime(Date.from(now))
          .expirationTime(Date.from(expiry))
          .build();

      JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
      SignedJWT signedJwt = new SignedJWT(jwsHeader, claimsSet);

      JWSSigner signer = new MACSigner(JWT_SECRET.getBytes());
      signedJwt.sign(signer);

      return signedJwt.serialize();
    } catch (JOSEException e) {
      throw new AppException(ErrorCode.FAILED_GENERATE_TOKEN);
    }
  }

  /**
   * Kiểm tra token và trả về JWTClaimsSet nếu token hợp lệ.
   *
   * @param token Chuỗi token cần kiểm tra
   * @return JWTClaimsSet nếu token hợp lệ
   */
  public JWTClaimsSet validateTokenAndGetClaims(String token) {
    try {
      SignedJWT signedJwt = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(JWT_SECRET.getBytes());

      if (!signedJwt.verify(verifier)) {
        throw new AppException(ErrorCode.INVALID_TOKEN_SIGNATURE);
      }

      Date exp = signedJwt.getJWTClaimsSet().getExpirationTime();
      if (exp.before(new Date())) {
        throw new AppException(ErrorCode.TOKEN_EXPIRED);
      }

      return signedJwt.getJWTClaimsSet();
    } catch (Exception e) {
      throw new AppException(ErrorCode.FAILED_VALIDATE_TOKEN);
    }
  }
}
