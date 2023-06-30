package sk.avo.chatapi.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.avo.chatapi.domain.user.models.UserModel;
import java.util.Optional;


@Repository
public interface IUserRepo extends JpaRepository<UserModel, Integer> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByEmail(String email);
}
