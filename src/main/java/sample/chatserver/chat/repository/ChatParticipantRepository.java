package sample.chatserver.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.chatserver.chat.domain.ChatParticipant;
import sample.chatserver.chat.domain.ChatRoom;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
  List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
}
