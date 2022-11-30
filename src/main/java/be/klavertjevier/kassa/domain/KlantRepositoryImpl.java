package be.klavertjevier.kassa.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class KlantRepositoryImpl implements KlantRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Klant> findMetOnbetaaldeOrder() {
		return entityManager.createQuery("SELECT k FROM Klant k WHERE EXISTS (SELECT o FROM Order o WHERE o.klant = k and o.betaald = false)", Klant.class).getResultList();
	}

}
