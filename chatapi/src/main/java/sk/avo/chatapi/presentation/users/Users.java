package sk.avo.chatapi.presentation.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.application.dto.TokenPair;
import sk.avo.chatapi.domain.security.exceptions.InvalidToken;
import sk.avo.chatapi.domain.user.exceptions.*;
import sk.avo.chatapi.domain.user.models.UserModel;
import sk.avo.chatapi.presentation.users.dto.*;

@RestController
@RequestMapping("/api/")
public class Users {
    private final ApplicationService applicationService;

    @Autowired
    public Users(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        TokenPair tokenPair;
        try {
            tokenPair = applicationService.signup(
                    signupRequest.getUsername(),
                    signupRequest.getPassword(),
                    signupRequest.getEmail()
            );
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.created(null).body(
                new SignupResponse(
                    tokenPair.getAccessToken(),
                    tokenPair.getRefreshToken()
                )
        );
    }

    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(
            Authentication authentication,
            @RequestBody VerifyEmailRequest verifyEmailRequest
    ) {
        UserModel userModel = (UserModel) authentication.getPrincipal();
        try {
            applicationService.verifyEmail(
                    userModel.getEmail(),
                    verifyEmailRequest.getCode()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserEmailVerifyException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("{}");
    }

    @PostMapping("/email/resend-code")
    public ResponseEntity<ResendEmailResponse> resendEmail(
            Authentication authentication
    ) {
        UserModel userModel = (UserModel) authentication.getPrincipal();
        if (userModel == null) return ResponseEntity.badRequest().build();

        try {
            userModel = applicationService.regenerateEmailVerificationCode(
                    userModel.getEmail()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserEmailIsAlreadyVerifiedException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        TokenPair tokenPair;
        try {
            tokenPair = applicationService.login(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UserIsNotVerifiedException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                new LoginResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
            )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest){
        TokenPair tokenPair;
        try {
            tokenPair = applicationService.refresh(
                    refreshRequest.getRefreshToken()
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidToken e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(
                new RefreshResponse(
                        tokenPair.getAccessToken(),
                        tokenPair.getRefreshToken()
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        UserModel userModel = (UserModel) authentication.getPrincipal();
        if (userModel == null) return ResponseEntity.badRequest().build();
        MeResponse meResponse = new MeResponse();
        meResponse.setId(userModel.getId());
        meResponse.setUsername(userModel.getUsername());
        meResponse.setIsVerified(userModel.getIsVerified());
        meResponse.setEmail(userModel.getEmail());
        meResponse.setPasswordHash(userModel.getPasswordHash());
        return ResponseEntity.ok(meResponse);
    }
}
