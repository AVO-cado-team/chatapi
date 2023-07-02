package sk.avo.chatapi.presentation.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int statusCode;
    private String message;
}
