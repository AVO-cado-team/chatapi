package sk.avo.chatapi.presentation.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendEmailRequest {
    private String email;
}
