package sk.avo.chatapi.application;

import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.user.UserService;
import sk.avo.chatapi.domain.user.exceptions.UserAlreadyExistsException;
import sk.avo.chatapi.domain.user.exceptions.UserEmailVerifyException;
import sk.avo.chatapi.domain.user.exceptions.UserIsNotVerifiedException;
import sk.avo.chatapi.domain.user.exceptions.UserNotFoundException;


@Service
public class ApplicationService {
    private final UserService userService;

    public ApplicationService(UserService userService) {
        this.userService = userService;
    }

    public void signup(
            String username,
            String password,
            String email
    ) throws UserAlreadyExistsException {
        userService.createUser(username, password, email);
    }

    public void verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException {
        userService.verifyEmail(email, code);
    }

    public void login(String username, String password) throws UserNotFoundException, UserIsNotVerifiedException {
        userService.getUserByUsernameAndPassword(username, password);
    }
}
