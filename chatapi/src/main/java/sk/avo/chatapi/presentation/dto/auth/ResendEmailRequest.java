package sk.avo.chatapi.presentation.dto.auth;

import lombok.Data;

@Data
public class ResendEmailRequest {
  private String email;
}
