package diego.newsproject;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import entities.Articoli;

@Repository
public interface ArticoliRepository extends CrudRepository<Articoli, Long> {
}
