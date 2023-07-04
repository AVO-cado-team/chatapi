package sk.avo.chatapi.presentation.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
  private String username;
  private String password;
  private String email;

  public SignupRequest() {}
}
