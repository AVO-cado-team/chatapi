package sk.avo.chatapi.application.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.security.TokenType;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.service.JwtTokenService;
import sk.avo.chatapi.domain.shared.Tuple;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
  private static final Logger logger = LoggerFactory.getLogger(JwtTokenServiceImpl.class);
  private final Algorithm hmac512;
  private final JWTVerifier verifier;
  private final Duration jwtAccessTokenValidity;
  private final Duration jwtRefreshTokenValidity;

  public JwtTokenServiceImpl(
          @Value("${jwt.secret}") final String secret,
          @Value("${jwt.validity.access-token}") final Duration accessTokenValidity,
          @Value("${jwt.validity.refresh-token}") final Duration refreshTokenValidity
          ) {
    this.hmac512 = Algorithm.HMAC512(secret);
    this.verifier = JWT.require(this.hmac512).build();
    this.jwtAccessTokenValidity = accessTokenValidity;
    this.jwtRefreshTokenValidity = refreshTokenValidity;
  }

  public String generateAccessToken(final UserId userId) {
    final Instant now = Instant.now();
    return JWT.create()
        .withSubject(userId.getValue().toString() + ":" + TokenType.ACCESS)
        .withIssuer("app")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(jwtAccessTokenValidity.toMillis()))
        .sign(this.hmac512);
  }

  public String generateRefreshToken(final UserId userId) {
    final Instant now = Instant.now();
    return JWT.create()
        .withSubject(userId.getValue().toString() + ":" + TokenType.REFRESH)
        .withIssuer("app")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(jwtRefreshTokenValidity.toMillis()))
        .sign(this.hmac512);
  }

  public Tuple<Long, String> validateTokenAndGetUserIdAndTokenType(final String token)
      throws InvalidTokenException {
    try {
      String payload = verifier.verify(token).getSubject();
      String[] parts = payload.split(":");
      return new Tuple<>(Long.parseLong(parts[0]), parts[1]);
    } catch (final JWTVerificationException verificationEx) {
      logger.warn("token invalid: {}", verificationEx.getMessage());
      throw new InvalidTokenException();
    } catch (final Exception ex) {
      logger.error("token invalid: {}", ex.getMessage());
      throw new InvalidTokenException();
    }
  }

  public Duration getJwtAccessTokenValidity() {
    return jwtAccessTokenValidity;
  }

  public Duration getJwtRefreshTokenValidity() {
    return jwtRefreshTokenValidity;
  }
}
