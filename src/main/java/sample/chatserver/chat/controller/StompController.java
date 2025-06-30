package sample.chatserver.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import sample.chatserver.chat.dto.ChatMessageReqDto;

@Controller
public class StompController {

  private final SimpMessageSendingOperations messageTemplate;

  public StompController(SimpMessageSendingOperations messageTemplate) {
    this.messageTemplate = messageTemplate;
  }

  //방법1.MessageMapping(수신)과 SenTo(topic에 메시지전달)한꺼번에 처리
  //  @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지 발행 시 MessageMapping 수신
  //  @SendTo("/topic/{roomId}") // 해당 roomId 에 메시지 발행하여 구독중인 클라이언트에게 메시지 전송
  //  // @DestinationVariable : @MessageMapping 으로 정의된 websocket controller 내에서만 사용
  //  public String sendMessage(@DestinationVariable Long roomId, String message) {
  //    System.out.println(message);
  //    return message;
  //  }

  // 방법2.MessageMapping어노테이션만 활용.
  @MessageMapping("/{roomId}")
  public void sendMessage(@DestinationVariable Long roomId, ChatMessageReqDto chatMessageReqDto) {
    System.out.println(chatMessageReqDto.getMessage());
    messageTemplate.convertAndSend("/topic/"+roomId, chatMessageReqDto);
  }

}
