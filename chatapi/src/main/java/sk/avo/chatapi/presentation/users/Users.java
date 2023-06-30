package sk.avo.chatapi.presentation.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.user.exceptions.UserAlreadyExistsException;
import sk.avo.chatapi.domain.user.exceptions.UserEmailVerifyException;
import sk.avo.chatapi.domain.user.exceptions.UserIsNotVerifiedException;
import sk.avo.chatapi.domain.user.exceptions.UserNotFoundException;
import sk.avo.chatapi.domain.user.models.UserModel;
import sk.avo.chatapi.presentation.users.dto.*;

@RestController("/api/users")
public class Users {
    private final ApplicationService applicationService;

    @Autowired
    public Users(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<NewUser> signup(@RequestBody SignupRequest signupRequest) {
        UserModel userModel;
        try {
            userModel = applicationService.signup(
                    signupRequest.getUsername(),
                    signupRequest.getPassword(),
                    signupRequest.getEmail()
            );
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.created(null).body(
                new NewUser(
                        userModel.getId(),
                        userModel.getUsername(),
                        userModel.getIsVerified()
                )
        );
    }

    @PostMapping("/email/verify")
    public ResponseEntity<BaseUser> verifyEmail(@RequestBody VerifyEmailRequest signupRequest) {
        UserModel userModel;
        try {
            userModel = applicationService.verifyEmail(
                    signupRequest.getEmail(),
                    signupRequest.getCode()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserEmailVerifyException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                new BaseUser(
                        userModel.getId(),
                        userModel.getUsername(),
                        userModel.getIsVerified()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUser> login(@RequestBody LoginRequest loginRequest) {
        UserModel userModel;
        try {
            userModel = applicationService.login(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserIsNotVerifiedException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                new LoginUser(
                        userModel.getId(),
                        userModel.getUsername(),
                        userModel.getIsVerified(),
                        userModel.getCreatedAt()
                )
        );
    }
}
