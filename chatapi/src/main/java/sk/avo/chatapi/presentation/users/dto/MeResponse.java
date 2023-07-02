package sk.avo.chatapi.presentation.users.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeResponse {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Boolean isVerified;
}
