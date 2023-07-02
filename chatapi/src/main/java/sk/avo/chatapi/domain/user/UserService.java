package sk.avo.chatapi.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.avo.chatapi.domain.user.models.UserModel;
import sk.avo.chatapi.domain.user.exceptions.UserAlreadyExistsException;
import sk.avo.chatapi.domain.user.exceptions.UserNotFoundException;
import sk.avo.chatapi.domain.user.exceptions.UserEmailVerifyException;
import sk.avo.chatapi.domain.user.exceptions.UserIsNotVerifiedException;
import sk.avo.chatapi.domain.user.exceptions.UserEmailIsAlreadyVerifiedException;


import java.util.Optional;


@Service
public class UserService {
    private final IUserRepo userRepo;
    private final IVerifyEmailRepo verifyEmailRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(IUserRepo userRepo, IVerifyEmailRepo verifyEmailRepo) {
        this.userRepo = userRepo;
        this.verifyEmailRepo = verifyEmailRepo;
    }

    @Transactional
    public UserModel createUser(
            String username,
            String password,
            String email
    ) throws UserAlreadyExistsException {
        if (userRepo.findByUsername(username).isPresent() | userRepo.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setIsVerified(false);
        user.setCreatedAt(java.time.LocalDateTime.now());

        verifyEmailRepo.addEmail(user.getEmail());
        verifyEmailRepo.generateCode(user.getEmail());
        user = userRepo.save(user);
        return user;
    }

    @Transactional
    public UserModel verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException {
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
    public UserModel getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException, UserIsNotVerifiedException {
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

    public UserModel regenerateEmailVerificationCode(String email) throws UserNotFoundException, UserEmailIsAlreadyVerifiedException {
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
