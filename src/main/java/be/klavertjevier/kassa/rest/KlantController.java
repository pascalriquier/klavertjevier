package be.klavertjevier.kassa.rest;


import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import be.klavertjevier.kassa.domain.Klant;
import be.klavertjevier.kassa.domain.KlantRepository;
import be.klavertjevier.kassa.domain.Order;

@RestController
public class KlantController {

	@Autowired
	private KlantRepository klantRepository;
	
	@DeleteMapping("/rest/klanten")
	public void deleteKlanten() {
		klantRepository.deleteAll();
	}
	
	@PostMapping(value = "/rest/klant")
	public Klant addKlant(@RequestBody Klant klant) {
		if (!klantRepository.findByNummerContainingIgnoreCase(klant.getNummer()).isEmpty()) {
			throw new IllegalArgumentException("Klant met nummer " + klant.getNummer() + " bestaat al");
		}
		return klantRepository.save(klant);
	}

	@DeleteMapping(value = "/rest/klant/{id}")
	public void deleteKlant(@PathVariable Integer id) {
		klantRepository.delete(klantRepository.findById(id).orElse(null));
	}

	@GetMapping(value = "/rest/klant/{id}")
	public Klant getKlant(@PathVariable Integer id) {
		return klantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Klant bestaat niet"));
	}

	@GetMapping(value = "/rest/klant/{id}/orders")
	public List<Order> getOrdersVanKlant(@PathVariable Integer id) {
		return klantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Klant bestaat niet")).getOrders();
	}

	@GetMapping(value = "/rest/klant")
	public List<Klant> zoekKlant(@RequestParam(name = "naam", required = false) String naam, @RequestParam(name = "nummer", required = false) String nummer) {
		if (!isEmpty(naam)) {
			if (isEmpty(nummer)) {
				return klantRepository.findByNaamContainingIgnoreCase(naam);
			} else {
				return klantRepository.findByNaamContainingIgnoreCaseAndNummerContainingIgnoreCase(naam, nummer);
			}
		} else {
			if (isEmpty(nummer)) {
				return new ArrayList<Klant>();
			} else {
				return klantRepository.findByNummerContainingIgnoreCase(nummer);
			}
		}
	}

	@GetMapping(value = "/rest/wanbetalers")
	public List<Klant> wanbetalers() {
		return klantRepository.findMetOnbetaaldeOrder();
	}

}
