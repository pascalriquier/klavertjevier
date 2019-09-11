package be.klavertjevier.kassa.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private LocalDate datum = LocalDate.now();
	private BigDecimal voorschot;
	@ElementCollection
	private List<OrderLijn> orderLijnen;

	public Order(List<OrderLijn> orderLijnen) {
		this();
		this.orderLijnen = orderLijnen;
	}
	
	public Order(LocalDate datum, List<OrderLijn> orderLijnen) {
		super();
		this.datum = datum;
		this.orderLijnen = orderLijnen;
	}

	public Order() {
	}

	public LocalDate getDatum() {
		return datum;
	}

	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}

	public List<OrderLijn> getOrderLijnen() {
		return orderLijnen;
	}

	public void setOrderLijnen(List<OrderLijn> orderLijnen) {
		this.orderLijnen = orderLijnen;
	}

	public BigDecimal getVoorschot() {
		return voorschot;
	}
	
	public void setVoorschot(BigDecimal voorschot) {
		this.voorschot = voorschot;
	}
}
