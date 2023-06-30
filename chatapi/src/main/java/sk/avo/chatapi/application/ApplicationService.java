package sk.avo.chatapi.application;

import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.user.UserService;
import sk.avo.chatapi.domain.user.exceptions.*;
import sk.avo.chatapi.domain.user.models.UserModel;


@Service
public class ApplicationService {
    private final UserService userService;

    public ApplicationService(UserService userService) {
        this.userService = userService;
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

    public UserModel login(String username, String password) throws UserNotFoundException, UserIsNotVerifiedException {
        return userService.getUserByUsernameAndPassword(username, password);
    }

    public UserModel regenerateEmailVerificationCode(String email) throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
        return userService.regenerateEmailVerificationCode(email);
    }
}
