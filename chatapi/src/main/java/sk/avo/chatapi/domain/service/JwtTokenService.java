package sk.avo.chatapi.domain.service;

import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.shared.Tuple;

import java.time.Duration;

public interface JwtTokenService {
  String generateAccessToken(final UserId userId);
  String generateRefreshToken(final UserId userId);
  Tuple<UserId, String> validateTokenAndGetUserIdAndTokenType(final String token) throws InvalidTokenException;
  Duration getJwtAccessTokenValidity();
  Duration getJwtRefreshTokenValidity();
}
