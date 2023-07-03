package sk.avo.chatapi.presentation.auth.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SignupResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public SignupResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
