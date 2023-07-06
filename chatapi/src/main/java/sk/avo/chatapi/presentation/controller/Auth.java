package sk.avo.chatapi.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.avo.chatapi.application.ApplicationService;
import sk.avo.chatapi.application.dto.TokenPair;
import sk.avo.chatapi.domain.model.security.InvalidTokenException;
import sk.avo.chatapi.domain.model.user.*;
import sk.avo.chatapi.presentation.dto.auth.*;

@RestController
@RequestMapping("/api/auth/")
public class Auth {
  private final ApplicationService applicationService;

  @Autowired
  public Auth(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @PostMapping("/signup")
  public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
    TokenPair tokenPair;
    try {
      tokenPair =
          applicationService.signup(
              signupRequest.getUsername(), signupRequest.getPassword(), signupRequest.getEmail());
    } catch (UserAlreadyExistsException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.created(null)
        .body(new SignupResponse(tokenPair.getAccessToken(), tokenPair.getRefreshToken()));
  }

  @PostMapping("/email/verify")
  public ResponseEntity<String> verifyEmail(
      Authentication authentication, @RequestBody VerifyEmailRequest verifyEmailRequest) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    try {
      applicationService.verifyEmail(userEntity.getEmail(), verifyEmailRequest.getCode());
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserEmailVerifyException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok("{}");
  }

  @PostMapping("/email/resend-code")
  public ResponseEntity<ResendEmailResponse> resendEmail(Authentication authentication) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    if (userEntity == null) return ResponseEntity.badRequest().build();

    try {
      applicationService.regenerateEmailVerificationCode(userEntity.getEmail());
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserEmailIsAlreadyVerifiedException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    TokenPair tokenPair;
    try {
      tokenPair = applicationService.login(loginRequest.getUsername(), loginRequest.getPassword());
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (UserIsNotVerifiedException e) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(
        new LoginResponse(tokenPair.getAccessToken(), tokenPair.getRefreshToken()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
    TokenPair tokenPair;
    try {
      tokenPair = applicationService.refresh(refreshRequest.getRefreshToken());
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (InvalidTokenException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok(
        new RefreshResponse(tokenPair.getAccessToken(), tokenPair.getRefreshToken()));
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> me(Authentication authentication) {
    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
    if (userEntity == null) return ResponseEntity.badRequest().build();
    MeResponse meResponse = new MeResponse();
    meResponse.setId(userEntity.getId());
    meResponse.setUsername(userEntity.getUsername());
    meResponse.setIsVerified(userEntity.getIsVerified());
    meResponse.setEmail(userEntity.getEmail());
    meResponse.setPasswordHash(userEntity.getPasswordHash());
    return ResponseEntity.ok(meResponse);
  }
}
