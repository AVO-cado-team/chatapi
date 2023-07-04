package sk.avo.chatapi.domain.service;

import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.user.UserModel;
import sk.avo.chatapi.domain.repository.UserRepo;
import sk.avo.chatapi.domain.repository.VerifyEmailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sk.avo.chatapi.domain.model.user.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.avo.chatapi.domain.model.user.UserEmailVerifyException;
import sk.avo.chatapi.domain.model.user.UserIsNotVerifiedException;
import sk.avo.chatapi.domain.model.user.UserAlreadyExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sk.avo.chatapi.domain.model.user.UserEmailIsAlreadyVerifiedException;

import java.util.Optional;

@Service
public class UserService {
  private final UserRepo userRepo;
  private final VerifyEmailRepo verifyEmailRepo;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserService(UserRepo userRepo, VerifyEmailRepo verifyEmailRepo) {
    this.userRepo = userRepo;
    this.verifyEmailRepo = verifyEmailRepo;
  }

  @Transactional
  public UserModel createUser(String username, String password, String email)
      throws UserAlreadyExistsException {
    if ((userRepo.findByUsername(username).isPresent() || userRepo.findByEmail(email).isPresent())
        && userRepo.findByEmail(email).get().getIsVerified()) {
      throw new UserAlreadyExistsException();
    }
    if (userRepo.findByEmail(email).isPresent()
        && !userRepo.findByEmail(email).get().getIsVerified()) {
      userRepo.delete(userRepo.findByEmail(email).get());
      userRepo.flush();
    }
    UserModel user = new UserModel();
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setEmail(email);

    verifyEmailRepo.addEmail(user.getEmail());
    verifyEmailRepo.generateCode(user.getEmail());
    user = userRepo.save(user);
    return user;
  }

  @Transactional
  public UserModel verifyEmail(String email, String code)
      throws UserNotFoundException, UserEmailVerifyException {
    Optional<UserModel> user = userRepo.findByEmail(email);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    if (!verifyEmailRepo.verifyEmail(email, code)) {
      throw new UserEmailVerifyException();
    }
    user.get().setIsVerified(true);
    userRepo.save(user.get());
    return user.get();
  }

  @Transactional
  public UserModel getUserByUsernameAndPassword(String username, String password)
      throws UserNotFoundException, UserIsNotVerifiedException {
    Optional<UserModel> user = userRepo.findByUsername(username);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    if (!passwordEncoder.matches(password, user.get().getPasswordHash())) {
      throw new UserNotFoundException();
    }
    if (!user.get().getIsVerified()) {
      throw new UserIsNotVerifiedException();
    }
    return user.get();
  }

  @Transactional
  public UserModel getUserByUsername(String username) throws UserNotFoundException {
    Optional<UserModel> user = userRepo.findByUsername(username);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    return user.get();
  }

  @Transactional
  public UserModel getUserById(Long id) throws UserNotFoundException {
    Optional<UserModel> user = userRepo.findById(id);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    return user.get();
  }

  public UserModel regenerateEmailVerificationCode(String email)
      throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
    Optional<UserModel> user = userRepo.findByEmail(email);
    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }
    if (user.get().getIsVerified()) {
      throw new UserEmailIsAlreadyVerifiedException();
    }
    if (!verifyEmailRepo.emailExists(email)) {
      verifyEmailRepo.addEmail(email);
    }
    verifyEmailRepo.generateCode(email);
    return user.get();
  }
}
