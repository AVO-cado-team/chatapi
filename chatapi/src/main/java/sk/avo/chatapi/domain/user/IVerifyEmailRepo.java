package sk.avo.chatapi.domain.user;


public interface IVerifyEmailRepo {
    boolean addEmail(String email);
    boolean verifyEmail(String email, String code);
    boolean generateCode(String email);
    boolean emailExists(String email);
}
