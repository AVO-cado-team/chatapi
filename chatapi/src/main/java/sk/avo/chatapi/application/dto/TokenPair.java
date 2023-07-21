package sk.avo.chatapi.application.dto;

import lombok.Data;

@Data
public class TokenPair {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";
}
