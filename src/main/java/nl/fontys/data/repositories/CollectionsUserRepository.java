package nl.fontys.data.repositories;

import nl.fontys.data.InMemoryCollectionsDatabase;
import nl.fontys.models.entities.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CollectionsUserRepository implements IUserRepository  {
    private InMemoryCollectionsDatabase datasource;

    public CollectionsUserRepository(){
        datasource = InMemoryCollectionsDatabase.getInstance();
    }

    @Override
    public boolean existsByEmail(String email) {
        return datasource.isEmailInUse(email);
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        return datasource.login(email, password);
    }

    @Override
    public List<User> findAllByUserNameContaining(String userName) {
        return datasource.findAllUsersByUserName(userName);
    }

    @Override
    public Optional<User> findByUserName(final String userName) {
        return datasource.findByUserName(userName);
    }

    @Override
    public <S extends User> S save(S s) {
        return datasource.save(s);
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return datasource.findUserById(uuid) != null ?
                Optional.of(datasource.findUserById(uuid))
                : Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return datasource.findUserById(uuid) != null;
    }

    @Override
    public Iterable<User> findAll() {
        return datasource.findAllUsers();
    }

    @Override
    public Iterable<User> findAllById(Iterable<UUID> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(UUID uuid) {
        datasource.deleteUser(uuid);
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends User> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}
