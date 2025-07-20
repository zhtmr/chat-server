package sample.chatserver.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
  스프링과 stomp 는 내부적으로 세션관리한다.
  -> 연결/해제 이벤트를 기록 및 연결된 세션수를 확인할 목적으로 이벤트 리스너 생성 --> 로그, 디버깅 목적
 */
@Slf4j
@Component
public class StompEventListener {
  private final Set<String> sessions = ConcurrentHashMap.newKeySet();

  @EventListener
  public void connectHandle(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    sessions.add(accessor.getSessionId());
    log.info("connect SessionId() = {}", accessor.getSessionId());
    log.info("total sessions = {}", sessions.size());
  }

  @EventListener
  public void disconnectHandle(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    sessions.remove(accessor.getSessionId());
    log.info("disconnect SessionId() = {}", accessor.getSessionId());
    log.info("total sessions = {}", sessions.size());
  }
}
