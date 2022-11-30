package be.klavertjevier.kassa.domain;

import java.util.List;

public interface KlantRepositoryCustom {
	List<Klant> findMetOnbetaaldeOrder();
}
