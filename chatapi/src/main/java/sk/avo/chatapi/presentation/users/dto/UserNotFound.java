package sk.avo.chatapi.presentation.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserNotFound {
    private String message;
}
