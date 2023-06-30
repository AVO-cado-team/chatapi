package sk.avo.chatapi.presentation.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class LoginUser extends BaseUser {
    @Getter
    @Setter
    private LocalDateTime createdAt;

    public LoginUser(Integer id, String username, Boolean isVerified, LocalDateTime createdAt) {
        super(id, username, isVerified);
        this.createdAt = createdAt;
    }
}
