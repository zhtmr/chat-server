package sample.chatserver.auth.scheduler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sample.chatserver.auth.service.JwtService;

@Component
@Slf4j
public class TokenCleanupScheduler {

  private final JwtService jwtService;

  public TokenCleanupScheduler(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Scheduled(fixedRate = 3600000) // 1시간마다 실행
  public void cleanupExpiredTokens() {
    log.info("Starting cleanup of expired refresh tokens");
    try {
      jwtService.cleanupExpiredTokens();
      log.info("Expired refresh tokens cleanup completed");
    } catch (Exception e) {
      log.error("Error during token cleanup: ", e);
    }
  }
}
