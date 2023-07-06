package sk.avo.chatapi;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.repository.UserRepo;


@SpringBootApplication
public class App {

  private final UserRepo userRepo;
  private Logger LOG = LoggerFactory.getLogger(App.class);
  public App(UserRepo userRepo) {
    this.userRepo = userRepo;
  }
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @PostConstruct
  public void init() {
    // FIXME Remove this code
    String username = "user";
    String password = "user";
    String email = "user@user.com";
    UserEntity user = new UserEntity();
    user.setUsername(username);
    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setEmail(email);
    user = userRepo.save(user);
    user.setIsVerified(true);
    userRepo.save(user);
  }
}
