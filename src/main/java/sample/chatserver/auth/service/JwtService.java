package sample.chatserver.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.chatserver.auth.entity.RefreshToken;
import sample.chatserver.auth.repository.RefreshTokenRepository;
import sample.chatserver.common.JwtUtil;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class JwtService {
  @Value("${jwt.secretKey}")
  private String jwtSecret;

  @Value("${jwt.access-token-expiration:900000}") // 15분
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration:604800000}") // 7일
  private long refreshTokenExpiration;


  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;

  public JwtService(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtil = jwtUtil;
  }


  // Access Token 생성
  public String generateAccessToken(String username) {
    return jwtUtil.createJwt(username, accessTokenExpiration);
  }

  // Refresh Token 생성 및 저장
  public String generateRefreshToken(String username) {
    // 기존 Refresh Token 삭제
    refreshTokenRepository.deleteByUsername(username);

    String tokenValue = jwtUtil.createJwt(username, refreshTokenExpiration);

    RefreshToken refreshToken = RefreshToken.builder()
        .token(tokenValue)
        .username(username)
        .expiryDate(LocalDateTime.now()
            .plusSeconds(refreshTokenExpiration / 1000))
        .build();

    refreshTokenRepository.save(refreshToken);

    return tokenValue;
  }


  // 토큰에서 email 추출
  public String getEmailFromToken(String token) {
    return jwtUtil.extractEmail(token);
  }

  // Access Token 검증
  public boolean validateAccessToken(String token) {
    try {
      jwtUtil.extractAllClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Access token expired: {}", e.getMessage());
      return false;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid access token: {}", e.getMessage());
      return false;
    }
  }

  // Refresh Token 검증
  public boolean validateRefreshToken(String token) {
    try {
      // JWT 자체 검증
      jwtUtil.extractAllClaims(token);

      // DB에서 토큰 존재 여부 및 만료 확인
      Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
      if (refreshTokenOpt.isEmpty()) {
        log.warn("Refresh token not found in database");
        return false;
      }

      RefreshToken refreshToken = refreshTokenOpt.get();
      if (refreshToken.isExpired()) {
        log.warn("Refresh token expired in database");
        refreshTokenRepository.delete(refreshToken);
        return false;
      }

      return true;
    } catch (ExpiredJwtException e) {
      log.warn("Refresh token expired: {}", e.getMessage());
      return false;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid refresh token: {}", e.getMessage());
      return false;
    }
  }


  // 토큰 만료 확인
  public boolean isTokenExpired(String token) {
    try {
      return jwtUtil.isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return true;
    }
  }

  // Refresh Token 삭제
  public void deleteRefreshToken(String username) {
    refreshTokenRepository.deleteByUsername(username);
  }

  // 만료된 Refresh Token 정리 (스케줄러에서 사용)
  @Transactional
  public void cleanupExpiredTokens() {
    refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
  }

}
