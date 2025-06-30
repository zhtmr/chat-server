//package sample.chatserver.common.auth;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//  @Value("${jwt.secretKey}")
//  private String secretKey;
//
//  private SecretKey getSigningKey() {
//    byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
//    return Keys.hmacShaKeyFor(keyBytes);
//  }
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws ServletException, IOException {
//
//    String authorization = request.getHeader("Authorization");
//    if (authorization == null || !authorization.startsWith("Bearer ")) {
////      handleUnauthorizedResponse(response, "Authorization 헤더가 누락되었거나 잘못된 형식입니다.");
//      log.info("Authorization 헤더 누락");
//      filterChain.doFilter(request, response);
//      return; // 요청 처리 중단
//    }
//
//    String token = authorization.substring(7);
//    try {
//      // 토큰 검증, claims 추출
////      Claims claims = Jwts.parserBuilder()
////          .setSigningKey(secretKey)
////          .build()
////          .parseClaimsJws(token)
////          .getBody();
//
//      Claims claims = Jwts.parser()
//          .verifyWith(getSigningKey())
//          .build()
//          .parseSignedClaims(token)
//          .getPayload();
//      List<GrantedAuthority> authorities = new ArrayList<>();
//      String role = claims.get("role", String.class);
////      if (role == null) {
//////        handleUnauthorizedResponse(response, "JWT 토큰에 role 정보가 누락되었습니다.");
////        filterChain.doFilter(request, response);
////        return;
////      }
//
//      authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//
//      UserDetails userDetails = new User(claims.getSubject(), "", authorities);
//      Authentication authToken =
//          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//      SecurityContextHolder.getContext().setAuthentication(authToken);
//    } catch (SignatureException e) {
//      e.printStackTrace();
//      handleUnauthorizedResponse(response, "서명이 유효하지 않은 JWT 토큰입니다.");
//      return;
//    } catch (Exception e) {
//      e.printStackTrace();
//      handleUnauthorizedResponse(response, "유효하지 않은 JWT 토큰입니다.");
//      return;
//    }
//
//    filterChain.doFilter(request, response);
//  }
//
//  private void handleUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
//    response.setCharacterEncoding("UTF-8");
//    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//    response.setContentType("application/json");
//    response.getWriter()
//        .write(MessageFormat.format("'{'\"error\": \"{0}\"'}'", message));
//  }
//}
