package sample.chatserver.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sample.chatserver.common.JwtUtil;
import sample.chatserver.member.domain.Member;
import sample.chatserver.member.dto.MemberListResDto;
import sample.chatserver.member.dto.MemberLoginReqDto;
import sample.chatserver.member.dto.MemberSaveReqDto;
import sample.chatserver.member.service.MemberService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;
  private final JwtUtil jwtUtil;

  public MemberController(MemberService memberService, JwtUtil jwtUtil) {
    this.memberService = memberService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/create")
  public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
    Member member = memberService.create(memberSaveReqDto);
    return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
  }

//  @PostMapping("/login")
//  public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto loginReqDto) {
//    Member member = memberService.login(loginReqDto);
//
//    String jwtToken = jwtUtil.createJwt(member.getEmail(), member.getRole()
//        .toString());
//    Map<String, Object> loginInfo = new HashMap<>();
//    loginInfo.put("token", jwtToken);
//    loginInfo.put("id", member.getId());
//    return new ResponseEntity<>(loginInfo, HttpStatus.OK);
//  }

  @GetMapping("/list")
  public ResponseEntity<?> getMembers() {
    List<MemberListResDto> dtos = memberService.findAll();
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

}
