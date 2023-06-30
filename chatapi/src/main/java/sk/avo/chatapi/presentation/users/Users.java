package sk.avo.chatapi.presentation.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.domain.user.exceptions.UserAlreadyExistsException;
import sk.avo.chatapi.presentation.users.dto.NewUser;
import sk.avo.chatapi.presentation.users.dto.SignupRequest;

@RestController("/api/users")
public class Users {
    private final ApplicationService applicationService;

    @Autowired
    public Users(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<NewUser> signup(@RequestBody SignupRequest signupRequest) {
        try {
            applicationService.signup(
                    signupRequest.getUsername(),
                    signupRequest.getPassword(),
                    signupRequest.getEmail()
            );
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.created(null).build();
    }
}
