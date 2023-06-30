package sk.avo.chatapi.infrastructure.cache.verifyemail;

import org.springframework.stereotype.Repository;
import sk.avo.chatapi.domain.user.IVerifyEmailRepo;
import sk.avo.chatapi.infrastructure.cache.verifyemail.models.Email;

import java.util.ArrayList;
import java.util.Optional;


@Repository
public class VerifyEmailRepo implements IVerifyEmailRepo {
    private final ArrayList<Email> emailCache;

    public VerifyEmailRepo() {
        this.emailCache = new ArrayList<>();
    }

    private boolean isEmailInCache(String email) {
        for (Email emailObj : this.emailCache) {
            if (emailObj.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean addEmail(String email) {
        if (isEmailInCache(email))
            return false;
        Email emailObj = new Email(email);
        this.emailCache.add(emailObj);
        return true;
    }

    public boolean verifyEmail(String email, String code) {
        for (Email emailObj : this.emailCache) {
            if (emailObj.getEmail().equals(email)) {
                if (emailObj.isCodeValid(code)) {
                    this.emailCache.remove(emailObj);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean generateCode(String email) {
        for (Email emailObj : this.emailCache) {
            if (emailObj.getEmail().equals(email)) {
                emailObj.generateCode();
                return true;
            }
        }
        return false;
    }
}
