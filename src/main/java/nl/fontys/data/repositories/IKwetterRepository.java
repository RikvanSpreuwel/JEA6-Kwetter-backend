package nl.fontys.data.repositories;

import nl.fontys.models.Kwetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface IKwetterRepository extends CrudRepository<Kwetter, UUID> {
    List<Kwetter> findAllByAuthor_Id(final UUID authorId);
    List<Kwetter> findAllByMessageContaining(final String message);
}
