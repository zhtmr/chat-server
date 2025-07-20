package sample.chatserver.chat.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.chatserver.chat.domain.ChatMessage;
import sample.chatserver.chat.domain.ChatParticipant;
import sample.chatserver.chat.domain.ChatRoom;
import sample.chatserver.chat.domain.ReadStatus;
import sample.chatserver.chat.dto.ChatMessageReqDto;
import sample.chatserver.chat.repository.ChatMessageRepository;
import sample.chatserver.chat.repository.ChatParticipantRepository;
import sample.chatserver.chat.repository.ChatRoomRepository;
import sample.chatserver.chat.repository.ReadStatusRepository;
import sample.chatserver.member.domain.Member;
import sample.chatserver.member.repository.MemberRepository;

import java.util.List;

@Service
@Transactional
public class ChatService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatParticipantRepository chatParticipantRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MemberRepository memberRepository;

  public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository,
      ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository,
      MemberRepository memberRepository) {
    this.chatRoomRepository = chatRoomRepository;
    this.chatParticipantRepository = chatParticipantRepository;
    this.chatMessageRepository = chatMessageRepository;
    this.readStatusRepository = readStatusRepository;
    this.memberRepository = memberRepository;
  }

  public void saveMessage(Long roomId, ChatMessageReqDto chatMessageReqDto) {
    // 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new EntityNotFoundException("room cannot be found"));

    // 보낸사람 조회
    Member sender = memberRepository.findByEmail(chatMessageReqDto.getSenderEmail())
        .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

    // 메시지 저장
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoom(chatRoom)
        .member(sender)
        .content(chatMessageReqDto.getMessage())
        .build();

    chatMessageRepository.save(chatMessage);

    // 사용자별로 읽음여부 저장
    List<ChatParticipant> participants =  chatParticipantRepository.findByChatRoom(chatRoom);
    for (ChatParticipant c : participants) {
      ReadStatus readStatus = ReadStatus.builder()
          .chatRoom(chatRoom)
          .member(c.getMember())
          .chatMessage(chatMessage)
          .isRead(c.getMember().equals(sender))
          .build();
      readStatusRepository.save(readStatus);
    }

  }
}
