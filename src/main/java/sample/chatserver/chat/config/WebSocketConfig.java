//package sample.chatserver.chat.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//  private final SimpleWebSocketHandler simpleWebSocketHandler;
//
//  public WebSocketConfig(SimpleWebSocketHandler simpleWebSocketHandler) {
//    this.simpleWebSocketHandler = simpleWebSocketHandler;
//  }
//
//  @Override
//  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//    registry.addHandler(simpleWebSocketHandler, "/connect")
//        // websocket 프로토콜에 대한 별도의 cors 설정 (SecurityConfig 의 cors 설정은 http 요청에 한한 설정임.)
//        .setAllowedOrigins("http://localhost:3000");
//  }
//}
