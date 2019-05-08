package nl.fontys.data.repositories;

import nl.fontys.data.InMemoryCollectionsDatabase;
import nl.fontys.models.Kwetter;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.NotSupportedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CollectionsKwetterRepository implements IKwetterRepository {
    private InMemoryCollectionsDatabase datasource;

    public CollectionsKwetterRepository(){
        datasource = InMemoryCollectionsDatabase.getInstance();
    }

    @Override
    public List<Kwetter> findAllByAuthor_Id(UUID authorId) {
        return datasource.findAllKwettersByAuthorId(authorId);
    }

    @Override
    public List<Kwetter> findAllByMessageContaining(String message) {
        return datasource.findAllKwettersByMessage(message);
    }

    @Override
    public <S extends Kwetter> S save(S s) {
        return datasource.save(s);
    }

    @Override
    public <S extends Kwetter> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Kwetter> findById(UUID uuid) {
        return datasource.findKwetterById(uuid) != null ?
                Optional.of(datasource.findKwetterById(uuid))
                : Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return datasource.findKwetterById(uuid) != null;
    }

    @Override
    public Iterable<Kwetter> findAll() {
        return datasource.findAllKwetters();
    }

    @Override
    public Iterable<Kwetter> findAllById(Iterable<UUID> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(UUID uuid) {
        datasource.deleteKwetter(uuid);
    }

    @Override
    public void delete(Kwetter kwetter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends Kwetter> iterable) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}
