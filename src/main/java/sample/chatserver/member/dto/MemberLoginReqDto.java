package sample.chatserver.member.dto;

import lombok.Value;
import sample.chatserver.member.domain.Role;

import java.io.Serializable;

/**
 * DTO for {@link sample.chatserver.member.domain.Member}
 */
@Value
public class MemberLoginReqDto implements Serializable {
  String email;
  String password;
}
