package sample.chatserver.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
  private String name;

  @NotBlank(message = "email is required")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;

}
