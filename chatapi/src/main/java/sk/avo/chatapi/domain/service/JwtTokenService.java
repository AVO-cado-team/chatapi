package sk.avo.chatapi.domain.service;

import java.time.Duration;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.shared.Tuple;

public interface JwtTokenService {
  String generateAccessToken(final UserId userId);
  String generateRefreshToken(final UserId userId);
  Tuple<Long, String> validateTokenAndGetUserIdAndTokenType(final String token) throws InvalidTokenException;
  Duration getJwtAccessTokenValidity();
  Duration getJwtRefreshTokenValidity();
}
