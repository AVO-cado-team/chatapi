package sk.avo.chatapi.domain.user;


public interface IVerifyEmailRepo {
    public boolean addEmail(String email);
    public boolean verifyEmail(String email, String code);
    public boolean generateCode(String email);
}
