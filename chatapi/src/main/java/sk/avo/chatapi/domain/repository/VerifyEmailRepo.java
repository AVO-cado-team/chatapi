package sk.avo.chatapi.domain.repository;

public interface VerifyEmailRepo {
  boolean addEmail(String email);
  boolean verifyEmail(String email, String code);
  boolean generateCode(String email);
  boolean emailExists(String email);
}
