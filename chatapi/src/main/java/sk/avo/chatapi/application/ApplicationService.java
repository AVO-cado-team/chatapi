package sk.avo.chatapi.application;

import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.security.JwtTokenService;
import sk.avo.chatapi.domain.security.dto.Tuple;
import sk.avo.chatapi.domain.security.exceptions.InvalidToken;
import sk.avo.chatapi.domain.user.UserService;
import sk.avo.chatapi.domain.user.exceptions.*;
import sk.avo.chatapi.domain.user.models.*;
import sk.avo.chatapi.application.dto.*;


@Service
public class ApplicationService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final static Long ACCESS_TOKEN_EXPIRATION = 3600L;


    public ApplicationService(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    public TokenPair signup(
            String username,
            String password,
            String email
    ) throws UserAlreadyExistsException {
        UserModel userModel = userService.createUser(username, password, email);
        TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(userModel.getId()));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(userModel.getId()));
        return tokenPair;
    }

    public UserModel verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException {
        return userService.verifyEmail(email, code);
    }

    public UserModel regenerateEmailVerificationCode(String email) throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
        return userService.regenerateEmailVerificationCode(email);
    }

    public TokenPair login(String username, String password) throws UserNotFoundException, UserIsNotVerifiedException {
        final UserModel user = userService.getUserByUsernameAndPassword(username, password);
        final TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(user.getId()));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(user.getId()));
        return tokenPair;
    }

    public TokenPair refresh(String refreshToken) throws InvalidToken, UserNotFoundException {
        final Tuple<Long, String> tokenPayload = jwtTokenService.validateTokenAndGetUserIdAndTokenType(refreshToken);
        if (!tokenPayload.getSecond().equals("refresh")) {
            throw new InvalidToken();
        }
        final TokenPair tokenPair = new TokenPair();
        tokenPair.setAccessToken(jwtTokenService.generateAccessToken(tokenPayload.getFirst()));
        tokenPair.setRefreshToken(jwtTokenService.generateRefreshToken(tokenPayload.getFirst()));
        return tokenPair;
    }
}
