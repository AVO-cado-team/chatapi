package sk.avo.chatapi.domain.service;


import sk.avo.chatapi.domain.model.user.*;

public interface UserService {
  UserEntity createUser(String username, String password, String email) throws UserAlreadyExistsException;
  UserEntity verifyEmail(String email, String code) throws UserNotFoundException, UserEmailVerifyException;
  UserEntity getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException, UserIsNotVerifiedException;
  UserEntity getUserByUsername(String username) throws UserNotFoundException;
  UserEntity getUserById(UserId userId) throws UserNotFoundException;
  UserEntity regenerateEmailVerificationCode(String email) throws UserNotFoundException, UserEmailIsAlreadyVerifiedException;
}
