package sk.avo.chatapi.domain.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.springframework.security.core.userdetails.UserDetails;
import sk.avo.chatapi.domain.security.dto.Tuple;
import sk.avo.chatapi.domain.security.exceptions.InvalidToken;


import java.time.Duration;
import java.time.Instant;

@Service
public class JwtTokenService {
    private static final Duration JWT_ACCESS_TOKEN_VALIDITY = Duration.ofMinutes(20);
    private static final Duration JWT_REFRESH_TOKEN_VALIDITY = Duration.ofDays(30);
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public JwtTokenService(@Value("${jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    public String generateAccessToken(final Long userId) {
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(userId.toString() + ":access")
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_ACCESS_TOKEN_VALIDITY.toMillis()))
                .sign(this.hmac512);
    }

    public String generateRefreshToken(final Long userId) {
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(userId.toString() + ":refresh")
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_REFRESH_TOKEN_VALIDITY.toMillis()))
                .sign(this.hmac512);
    }
    public Tuple<Long, String> validateTokenAndGetUserIdAndTokenType(final String token) throws InvalidToken {
        try {
            String payload = verifier.verify(token).getSubject();
            String[] parts = payload.split(":");
            return new Tuple<>(Long.parseLong(parts[0]), parts[1]);
        } catch (final JWTVerificationException verificationEx) {
            logger.warn("token invalid: {}", verificationEx.getMessage());
            throw new InvalidToken();
        } catch (final Exception ex) {
            logger.error("token invalid: {}", ex.getMessage());
            throw new InvalidToken();
        }
    }

    public Duration getJwtAccessTokenValidity() {
        return JWT_ACCESS_TOKEN_VALIDITY;
    }
    public Duration getJwtRefreshTokenValidity() {
        return JWT_REFRESH_TOKEN_VALIDITY;
    }
}