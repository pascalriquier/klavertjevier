package be.klavertjevier.kassa.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KlantRepository extends CrudRepository<Klant, Integer>, KlantRepositoryCustom {

	List<Klant> findByNaamContainingIgnoreCase(String naam);

	List<Klant> findByNaamContainingIgnoreCaseAndNummerContainingIgnoreCase(String naam, String nummer);

	List<Klant> findByNummerContainingIgnoreCase(String nummer);

}
