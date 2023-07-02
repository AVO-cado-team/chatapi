package sk.avo.chatapi.presentation.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class BaseUser {
    @Getter
    private Long id;
    @Getter
    private String username;
    @Getter
    private Boolean isVerified;
}
