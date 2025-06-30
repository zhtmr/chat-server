package sample.chatserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.chatserver.member.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByEmail(String email);
}
