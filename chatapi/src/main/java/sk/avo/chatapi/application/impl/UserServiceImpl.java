package sk.avo.chatapi.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.avo.chatapi.domain.model.user.*;
import sk.avo.chatapi.domain.repository.UserRepo;
import sk.avo.chatapi.domain.repository.VerifyEmailRepo;
import sk.avo.chatapi.domain.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepo userRepo;
  private final VerifyEmailRepo verifyEmailRepo;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserServiceImpl(UserRepo userRepo, VerifyEmailRepo verifyEmailRepo) {
    this.userRepo = userRepo;
    this.verifyEmailRepo = verifyEmailRepo;
  }

  @Transactional
  public UserEntity createUser(String username, String password, String email)
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
    UserEntity user = new UserEntity();
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setEmail(email);
    verifyEmailRepo.addEmail(user.getEmail());
    verifyEmailRepo.generateCode(user.getEmail());
    user = userRepo.save(user);
    return user;
  }

  @Transactional
  public UserEntity verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException {
    Optional<UserEntity> user = userRepo.findByEmail(email);
    if (user.isEmpty()) throw new UserNotFoundException();
    if (!verifyEmailRepo.verifyEmail(email, code)) throw new UserEmailVerifyException();
    user.get().setIsVerified(true);
    userRepo.save(user.get());
    return user.get();
  }

  @Transactional
  public UserEntity getUserByUsernameAndPassword(String username, String password)
          throws UserNotFoundException, UserIsNotVerifiedException {
    Optional<UserEntity> user = userRepo.findByUsername(username);
    if (user.isEmpty()) throw new UserNotFoundException();
    if (!passwordEncoder.matches(password, user.get().getPasswordHash())) throw new UserNotFoundException();
    if (!user.get().getIsVerified()) throw new UserIsNotVerifiedException();
    return user.get();
  }

  @Transactional
  public UserEntity getUserByUsername(String username) throws UserNotFoundException {
    Optional<UserEntity> user = userRepo.findByUsername(username);
    if (user.isEmpty()) throw new UserNotFoundException();
    return user.get();
  }

  @Transactional
  public UserEntity getUserById(UserId userId) throws UserNotFoundException {
    Optional<UserEntity> user = userRepo.findById(userId.getValue());
    if (user.isEmpty()) throw new UserNotFoundException();
    return user.get();
  }

  public UserEntity regenerateEmailVerificationCode(String email)
      throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
    Optional<UserEntity> user = userRepo.findByEmail(email);
    if (user.isEmpty()) throw new UserNotFoundException();
    if (user.get().getIsVerified()) throw new UserEmailIsAlreadyVerifiedException();
    if (!verifyEmailRepo.emailExists(email)) verifyEmailRepo.addEmail(email);
    verifyEmailRepo.generateCode(email);
    return user.get();
  }

}
