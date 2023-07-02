package sk.avo.chatapi.presentation.users.dto;

public class NewUser extends BaseUser {
    public NewUser(Long id, String username, Boolean isVerified) {
        super(id, username, isVerified);
    }
}
