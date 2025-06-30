package sample.chatserver.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  public StompWebSocketConfig(StompHandler stompHandler) {
    this.stompHandler = stompHandler;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/connect")
        .setAllowedOrigins("http://localhost:3000")
        // ws:// 가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 sockJS를 통한 요청 허용 설정
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 메시지 발행 형태 설정
    // /publish 로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
    registry.setApplicationDestinationPrefixes("/publish");
    // 메시지 수신 형태 설정
    registry.enableSimpleBroker("/topic");
  }

  /*
    웹소켓 요청(connect, subscribe, disconnect) 등의 요청시에는 http header 등 http 메시지를 읽어올 수 있고,
    이를 가로채 토큰을 검증
    (로그인 된 사용자만 채팅할 수 있어야 한다.
    현재 SecurityConfig 에 '/connect/**' 부분은 인증 제외되어 있으므로 누구나 채팅이 가능한 상황.
     이후 채팅은 http 요청이 아니기 때문에(ws) security filter 에서는 검증 불가능하다.)
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompHandler);
  }
}
