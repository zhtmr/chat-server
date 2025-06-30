package sample.chatserver.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
  private String accessToken;
  private String refreshToken;
  @Builder.Default
  private String tokenType = "Bearer";
  private long expiresIn; // seconds
  private String username;
}
