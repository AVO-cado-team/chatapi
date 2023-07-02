package sk.avo.chatapi.application;

import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sk.avo.chatapi.domain.security.JwtTokenService;
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

    public UserModel signup(
            String username,
            String password,
            String email
    ) throws UserAlreadyExistsException {
        return userService.createUser(username, password, email);
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
        tokenPair.setExpiresIn(ACCESS_TOKEN_EXPIRATION);
        return tokenPair;
    }
}
