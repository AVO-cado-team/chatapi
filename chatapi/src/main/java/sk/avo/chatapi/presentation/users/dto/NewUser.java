package sk.avo.chatapi.presentation.users.dto;

public class NewUser extends BaseUser {
    public NewUser(Integer id, String username, String isVerified) {
        super(id, username, isVerified);
    }
}
