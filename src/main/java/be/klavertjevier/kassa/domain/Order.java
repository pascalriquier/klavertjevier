package be.klavertjevier.kassa.domain;

import static javax.persistence.EnumType.STRING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private LocalDateTime datum = LocalDateTime.now();
	private LocalDate dag = LocalDate.now();
	@ElementCollection
	private List<OrderLijn> orderLijnen;
	@ManyToOne(fetch = FetchType.EAGER)
	private Klant klant;
	@Enumerated(STRING)
	private BetaalWijze betaaldMet;
	private boolean betaald;
	private boolean geannuleerd;
	private boolean verwerkt;

	public Order(List<OrderLijn> orderLijnen) {
		this();
		this.orderLijnen = orderLijnen;
	}
	
	public Order(LocalDateTime datum, List<OrderLijn> orderLijnen) {
		super();
		this.datum = datum;
		this.orderLijnen = orderLijnen;
	}

	public Order() {
	}

	public Integer getId() {
		return id;
	}
	
	public LocalDateTime getDatum() {
		return datum;
	}
	
	public LocalDate getDag() {
		return dag;
	}
	
	public void setDag(LocalDate dag) {
		this.dag = dag;
	}

	public void setDatum(LocalDateTime datum) {
		this.datum = datum;
	}

	public List<OrderLijn> getOrderLijnen() {
		return orderLijnen;
	}

	public void setOrderLijnen(List<OrderLijn> orderLijnen) {
		this.orderLijnen = orderLijnen;
	}

	public Klant getKlant() {
		return klant;
	}

	public void setKlant(Klant klant) {
		this.klant = klant;
		this.klant.addOrder(this);
	}

	public void setBetaald(BetaalWijze betaalWijze) {
		this.betaald = true;
		this.betaaldMet = betaalWijze;
	}
	
	public boolean isBetaald() {
		return betaald;
	}
	
	public BetaalWijze getBetaaldMet() {
		return betaaldMet;
	}
	
	public boolean isGeannuleerd() {
		return geannuleerd;
	}
	
	public void setGeannuleerd(boolean geannuleerd) {
		this.geannuleerd = geannuleerd;
	}
	
	public boolean isVerwerkt() {
		return verwerkt;
	}
	
	public void setVerwerkt(boolean verwerkt) {
		this.verwerkt = verwerkt;
	}
}
