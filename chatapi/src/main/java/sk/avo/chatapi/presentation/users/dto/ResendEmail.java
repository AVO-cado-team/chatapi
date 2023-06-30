package sk.avo.chatapi.presentation.users.dto;


public class ResendEmail extends BaseUser{

    public ResendEmail(Integer id, String username, Boolean isVerified) {
        super(id, username, isVerified);
    }
}
