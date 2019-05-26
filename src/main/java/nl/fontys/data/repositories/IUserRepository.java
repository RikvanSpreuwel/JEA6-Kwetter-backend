package nl.fontys.data.repositories;

import nl.fontys.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends CrudRepository<User, UUID> {
    boolean existsByEmail(final String email);
    User findByEmailAndPassword(final String email, final String password);
    List<User> findAllByUserNameContaining(final String userName);
    Optional<User> findByUserName(final String userName);
}
