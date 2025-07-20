package sample.chatserver.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import sample.chatserver.common.domain.BaseTimeEntity;
import sample.chatserver.member.dto.MemberListResDto;
import sample.chatserver.member.dto.MemberSaveReqDto;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Member extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Role role = Role.USER;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public static Member create(MemberSaveReqDto memberSaveReqDto, PasswordEncoder passwordEncoder) {
    return Member.builder()
        .name(memberSaveReqDto.getName())
        .email(memberSaveReqDto.getEmail())
        .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
        .build();
  }

  public MemberListResDto toMemberListResDto() {
    return MemberListResDto.builder()
        .id(this.id)
        .name(this.name)
        .email(this.email)
        .build();
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
