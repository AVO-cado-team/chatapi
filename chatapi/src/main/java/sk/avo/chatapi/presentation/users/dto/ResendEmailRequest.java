package sk.avo.chatapi.presentation.users.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendEmailRequest {
    private String email;
}
