package sk.avo.chatapi.domain.model.security;

public class WrongTokenType extends BaseSecurityException {
    public WrongTokenType(String message) {
        super(message);
    }

    public WrongTokenType() {
        super("Wrong token type, you either used ACCESS token for refresh or REFRESH token for access token");
    }
}
