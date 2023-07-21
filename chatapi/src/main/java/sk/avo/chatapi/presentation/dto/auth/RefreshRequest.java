package sk.avo.chatapi.presentation.dto.auth;

import lombok.Data;

@Data
public class RefreshRequest {
  private String refreshToken;
}
