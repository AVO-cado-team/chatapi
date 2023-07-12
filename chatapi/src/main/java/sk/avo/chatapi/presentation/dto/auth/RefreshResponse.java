package sk.avo.chatapi.presentation.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public RefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
