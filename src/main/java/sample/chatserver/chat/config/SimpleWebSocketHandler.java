//package sample.chatserver.chat.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//// /connect 로 웹소켓 연결요청이 들어왔을때 이를 처리할 클래스
//@Slf4j
//@Component
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//
//  // 연결된 세션 관리
//  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//
//  @Override
//  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//    sessions.add(session);
//    log.info("connection established: {}", session.getId());
//  }
//
//  @Override
//  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//    String payload = message.getPayload();
//    log.info("receive message: {}", payload);
//    for (WebSocketSession s : sessions) {
//      if (s.isOpen()) {
//        s.sendMessage(new TextMessage(payload));
//      }
//    }
//  }
//
//  @Override
//  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//    sessions.remove(session);
//    log.info("connection closed: {}", session.getId());
//  }
//
//}
