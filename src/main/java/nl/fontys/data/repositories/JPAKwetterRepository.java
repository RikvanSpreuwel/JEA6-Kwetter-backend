package nl.fontys.data.repositories;

import nl.fontys.models.Kwetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JPAKwetterRepository extends IKwetterRepository {
}
