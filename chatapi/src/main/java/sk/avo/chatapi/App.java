package sk.avo.chatapi;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sk.avo.chatapi.domain.model.user.UserEntity;
import sk.avo.chatapi.domain.repository.UserRepo;


@SpringBootApplication
public class App {

  private final UserRepo userRepo;

    public App(UserRepo userRepo) {
    this.userRepo = userRepo;
  }
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @PostConstruct
  public void init() {
    // FIXME Remove this code
    final String username = "user";
    final String password = "user";
    final String email = "user@user.com";
    UserEntity user = new UserEntity();
    user.setUsername(username);
    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setEmail(email);
    user = userRepo.save(user);
    user.setIsVerified(true);
    userRepo.save(user);
  }


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NotNull CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:3000");
			}
		};
	}
}
