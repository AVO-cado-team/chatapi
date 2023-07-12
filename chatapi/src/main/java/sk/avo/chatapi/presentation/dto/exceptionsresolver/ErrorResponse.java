package sk.avo.chatapi.presentation.dto.exceptionsresolver;

import lombok.Data;

@Data
public class ErrorResponse {
  private int statusCode;
  private String message;
}
