package sk.avo.chatapi.presentation.exceptionsresolver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int statusCode;
  private String message;
}
