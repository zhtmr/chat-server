package sample.chatserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Slf4j
@SpringBootApplication(scanBasePackages = "sample")
public class ChatserverApplication {

  public static void main(String[] args) {
    // docker 로 실행시 passwordEncoder 를 못찾는 오류가 뜨길래 확인용으로 작성한 로직 -> docker-compose build --no-cache 로 해결
    ConfigurableApplicationContext context = SpringApplication.run(ChatserverApplication.class, args);
    String[] beanNames = context.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      if (beanName.contains("password")) {
        log.info("beanName {}", beanName);
      }
    }
  }



}
