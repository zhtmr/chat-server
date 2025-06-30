package sample.chatserver.chat.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Objects;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

  @Value("${jwt.secretKey}")
  private String secretKey;

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      log.info("connect 요청 시 토큰 유효성 검증");
      String bearerToken = accessor.getFirstNativeHeader("Authorization");
      String token = Objects.requireNonNull(bearerToken)
          .substring("Bearer ".length());
      // 토큰검증
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      log.info("토큰 검증 완료");
    }
    return message;
  }
}
