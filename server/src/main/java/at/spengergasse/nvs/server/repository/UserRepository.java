package at.spengergasse.nvs.server.repository;

import at.spengergasse.nvs.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The {@code UserRepository} provides all methods for CRUD operations by default for users.
 */

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
