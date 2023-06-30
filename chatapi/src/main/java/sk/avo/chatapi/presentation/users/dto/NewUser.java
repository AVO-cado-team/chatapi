package sk.avo.chatapi.presentation.users.dto;

public class NewUser extends BaseUser {
    public NewUser(Integer id, String username, Boolean isVerified) {
        super(id, username, isVerified);
    }
}
