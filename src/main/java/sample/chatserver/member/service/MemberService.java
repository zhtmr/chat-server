package sample.chatserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.chatserver.member.domain.Member;
import sample.chatserver.member.dto.MemberListResDto;
import sample.chatserver.member.dto.MemberLoginReqDto;
import sample.chatserver.member.dto.MemberSaveReqDto;
import sample.chatserver.member.repository.MemberRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member create(MemberSaveReqDto memberSaveReqDto) {
    if (memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already exists");
    }

    Member newMember = Member.create(memberSaveReqDto, passwordEncoder);
    return memberRepository.save(newMember);
  }

  public Member login(MemberLoginReqDto loginReqDto) {
    Member member = memberRepository.findByEmail(loginReqDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

    if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    return member;
  }

  public List<MemberListResDto> findAll() {
    return memberRepository.findAll()
        .stream()
        .map(Member::toMemberListResDto)
        .toList();
  }

}
