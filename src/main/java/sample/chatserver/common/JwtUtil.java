package sample.chatserver.common;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sample.chatserver.auth.exception.JwtTokenExpiredException;
import sample.chatserver.auth.exception.JwtTokenInvalidException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

  private final SecretKey secretKey;

  private static final long CLOCK_SKEW_ALLOWED = 300; // 5분

  // 생성자에서 비밀키 초기화
  public JwtUtil(@Value("${jwt.secretKey}") String secret) {
    this.secretKey = getSigningKey(secret);
  }

  private SecretKey getSigningKey(String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * JWT 토큰 생성
   *
   * @param email email
   * @param expiration  expiration
   * @return JWT 토큰 문자열
   */
  public String createJwt(String email, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);
    return Jwts.builder()
        .subject(email)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  /**
   * 토큰에서 사용자명 추출
   */
  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * 토큰에서 권한 추출
   */
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  /**
   * 토큰에서 만료 시간 추출
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * 토큰에서 발행 시간 추출
   */
  public Date extractIssuedAt(String token) {
    return extractClaim(token, Claims::getIssuedAt);
  }

  /**
   * 토큰에서 특정 클레임 추출
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }


  public Claims extractAllClaims(String token) {
      return Jwts.parser()
          .verifyWith(secretKey)
          .clockSkewSeconds(CLOCK_SKEW_ALLOWED)
          .build()
          .parseSignedClaims(token)
          .getPayload();
  }

  /**
   * 토큰 만료 여부 확인
   */
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * 토큰 유효성 검증
   */
  public boolean isTokenValid(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 토큰과 사용자명으로 토큰 유효성 검증
   */
  public boolean validateToken(String token, String username) {
    final String tokenUsername = extractEmail(token);
    return (username.equals(tokenUsername) && !isTokenExpired(token));
  }

  /**
   * 토큰 헤더에서 Bearer 토큰 추출
   */
  public String extractTokenFromHeader(String authorizationHeader) {
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return null;
  }

  /**
   * 토큰 남은 유효 시간 (밀리초)
   */
  public long getTokenRemainingTime(String token) {
    Date extractedExpiration = extractExpiration(token);
    return extractedExpiration.getTime() - System.currentTimeMillis();
  }



}
