package sample.chatserver.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sample.chatserver.auth.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUsername(String username);

  @Modifying
  @Transactional
  @Query("DELETE FROM RefreshToken rt WHERE rt.username = :username")
  void deleteByUsername(@Param("username") String username);

  @Modifying
  @Transactional
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
  void deleteByExpiryDateBefore(@Param("now") LocalDateTime now);

}
