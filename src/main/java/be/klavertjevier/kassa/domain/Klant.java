package be.klavertjevier.kassa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "klanten")
public class Klant {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String naam;
	
	private String nummer;
	
	@OneToMany(mappedBy = "klant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Order> orders = new ArrayList<Order>();

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getNummer() {
		return nummer;
	}

	public void setNummer(String nummer) {
		if (!nummer.matches("[0-9]{3}")) {
			throw new IllegalArgumentException("Klantennummer moet uit 3 cijfers bestaan");
		}
		this.nummer = nummer;
	}

	public Integer getId() {
		return id;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	
}
