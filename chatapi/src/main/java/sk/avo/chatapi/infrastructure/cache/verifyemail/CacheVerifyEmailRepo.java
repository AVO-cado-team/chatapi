package sk.avo.chatapi.infrastructure.cache.verifyemail;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import sk.avo.chatapi.infrastructure.cache.verifyemail.models.Email;

@Repository
public class CacheVerifyEmailRepo implements sk.avo.chatapi.domain.repository.VerifyEmailRepo {
  private final ArrayList<Email> emailCache;
  private final boolean testMode;
  private final String testCode;

  public CacheVerifyEmailRepo(@Value ("${verifyemail.test-mode}") boolean testMode,
                              @Value ("${verifyemail.test-code}") String testCode) {
    this.emailCache = new ArrayList<>();
    this.testMode = testMode;
    this.testCode = testCode;
  }

  private boolean isEmailInCache(String email) {
    return this.emailCache.stream().anyMatch(emailObj -> emailObj.getEmail().equals(email));
  }

  @Override
  public boolean addEmail(String email) {
    if (isEmailInCache(email)) return false;
    Email emailObj = new Email(email);
    this.emailCache.add(emailObj);
    return true;
  }

  @Override
  public boolean verifyEmail(String email, String code) {
    Email emailObj =
        this.emailCache.stream()
            .filter(emailObj1 -> emailObj1.getEmail().equals(email) && emailObj1.isCodeValid(code))
            .findFirst()
            .orElse(null);
    if (emailObj == null) return false;
    this.emailCache.remove(emailObj);
    return true;
  }

  @Override
  public boolean generateCode(String email) {
    Email emailObj =
        this.emailCache.stream()
            .filter(emailObj1 -> emailObj1.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    if (emailObj == null) return false;
    if (this.testMode) emailObj.setCode(this.testCode);
    else emailObj.generateCode();
    return true;
  }

  @Override
  public boolean emailExists(String email) {
    return this.isEmailInCache(email);
  }
}
