package sk.avo.chatapi.presentation.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupRequest {
  private String username;
  private String password;
  private String email;

  public SignupRequest() {}
}
