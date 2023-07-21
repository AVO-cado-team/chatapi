package sk.avo.chatapi.presentation.dto.auth;

import lombok.Data;

@Data
public class SignupResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";

  public SignupResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
