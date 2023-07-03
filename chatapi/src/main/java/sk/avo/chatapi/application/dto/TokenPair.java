package sk.avo.chatapi.application.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
