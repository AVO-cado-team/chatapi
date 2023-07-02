package sk.avo.chatapi.presentation.users.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefreshRequest {
    private String refreshToken;
}
