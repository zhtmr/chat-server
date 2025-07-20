package sample.chatserver.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.chatserver.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChatRoom extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Builder.Default
  private String isGroupChat = "N";

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
  private List<ChatParticipant> participants = new ArrayList<>();

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<ChatMessage> chatMessages = new ArrayList<>();

}
