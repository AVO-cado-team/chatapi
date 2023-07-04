package sk.avo.chatapi.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.avo.chatapi.domain.model.user.UserModel;

@Repository
public interface UserRepo extends JpaRepository<UserModel, Long> {
  Optional<UserModel> findByUsername(String username);

  Optional<UserModel> findByEmail(String email);
}
