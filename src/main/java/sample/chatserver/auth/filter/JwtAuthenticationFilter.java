package sample.chatserver.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    try {
      String authHeader = request.getHeader("Authorization");
      String token = jwtUtil.extractTokenFromHeader(authHeader);
      validateAndProcessToken(token);
    } catch (JwtTokenExpiredException e) {
      log.warn("JWT token expired: {}", e.getMessage());
      handleTokenException(response, "TOKEN_EXPIRED", "JWT token has expired");
      return;
    } catch (JwtTokenInvalidException e) {
      log.warn("JWT token invalid: {}", e.getMessage());
      handleTokenException(response, "TOKEN_INVALID", "JWT token is invalid");
      return;
    } catch (Exception e) {
      log.error("JWT authentication error: {}", e.getMessage());
      handleTokenException(response, "AUTHENTICATION_ERROR", "Authentication failed");
      return;
    }

    chain.doFilter(request, response);
  }

  private void validateAndProcessToken(String token) {
    if (jwtUtil.isTokenValid(token)) {

      // 1. 토큰 블랙리스트 확인 (Redis 사용)
      if (isTokenBlacklisted(token)) {
        throw new JwtTokenInvalidException("Token is blacklisted");
      }

      // 2. JWT 토큰에서 사용자 정보 추출
      String username = jwtUtil.extractUsername(token);
      String role = jwtUtil.extractRole(token);

      // 5. JWT 토큰 정보로 직접 UserDetails 생성
      Collection<GrantedAuthority> authorities =
          Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

      UserDetails userDetails = User.builder()
          .username(username)
          .password("")
          .authorities(authorities)
          .build();

      // 6. 인증 토큰 생성
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              authorities
          );

      // 7. 보안 컨텍스트에 인증 정보 설정
      SecurityContextHolder.getContext()
          .setAuthentication(authToken);

      log.debug("JWT authentication successful for user: {}", username);
    } else {
      throw new JwtTokenInvalidException("Token validation failed");
    }
  }

  private boolean isTokenBlacklisted(String token) {
    try {
      String tokenHash = DigestUtils.sha256Hex(token);
      return redisTemplate.hasKey("blacklist:" + tokenHash);
    } catch (Exception e) {
      log.warn("Error checking token blacklist: {}", e.getMessage());
      return false;
    }
  }

  private void handleTokenException(HttpServletResponse response,
      String errorCode,
      String message) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", errorCode);
    errorResponse.put("message", message);
    errorResponse.put("timestamp", Instant.now()
        .toString());

    response.getWriter()
        .write(objectMapper.writeValueAsString(errorResponse));
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/") ||
           path.startsWith("/api/public/") ||
           path.equals("/health") ||
           path.startsWith("/swagger-ui") ||
           path.startsWith("/v3/api-docs");
  }
}
