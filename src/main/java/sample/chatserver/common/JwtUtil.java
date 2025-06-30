package sample.chatserver.common.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

  private final SecretKey secretKey;
  private final int expiration;

  // 생성자에서 비밀키 초기화
  public JwtUtil(@Value("${jwt.secretKey}") String secret, @Value("${jwt.expiration}") int expiration) {
    this.secretKey = getSigningKey(secret);
    this.expiration = expiration;
  }

  private SecretKey getSigningKey(String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * JWT 토큰 생성
   *
   * @param email email
   * @param role  사용자 권한
   * @return JWT 토큰 문자열
   */
  public String createJwt(String email, String role) {
    Date now = new Date();
    return Jwts.builder()
        .subject(email)                              // 표준 sub 클레임
        .claim("role", role)                            // 사용자 권한
        .issuedAt(now) // 발행 시간
        .expiration(new Date(now.getTime() + expiration * 60 * 1000L)) // 만료 시간
        .signWith(secretKey)                            // 서명
        .compact();
  }

  /**
   * 토큰에서 사용자명 추출
   */
  public String extractUsername(String token) {
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


  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      log.warn("JWT token is expired: {}", e.getMessage());
      throw new JwtTokenExpiredException("JWT token is expired");
    } catch (UnsupportedJwtException e) {
      log.warn("JWT token is unsupported: {}", e.getMessage());
      throw new JwtTokenInvalidException("JWT token is unsupported");
    } catch (MalformedJwtException e) {
      log.warn("JWT token is malformed: {}", e.getMessage());
      throw new JwtTokenInvalidException("JWT token is malformed");
    } catch (SignatureException e) {
      log.warn("JWT signature does not match: {}", e.getMessage());
      throw new JwtTokenInvalidException("JWT signature validation failed");
    } catch (IllegalArgumentException e) {
      log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
      throw new JwtTokenInvalidException("JWT token is invalid");
    }
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
    final String tokenUsername = extractUsername(token);
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
