package at.spengergasse.nvs.server.repository;

import at.spengergasse.nvs.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findUserByUsernameAndPassword(String username, String password);

}
