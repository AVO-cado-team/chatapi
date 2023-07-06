package sk.avo.chatapi.presentation.dto.exceptionsresolver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int statusCode;
  private String message;
}
