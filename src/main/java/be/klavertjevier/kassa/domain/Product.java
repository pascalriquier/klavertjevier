package be.klavertjevier.kassa.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Product {
	public enum ProductType {
		VOORGERECHT, HOOFDGERECHT, DESSERT, DRANK
	}
	
	
	@Id
	private int code;
	
	private String naam;
	
	private BigDecimal prijs;
	
	@Enumerated(EnumType.STRING)
	private ProductType type;

	public Product(int code, String naam, BigDecimal prijs, ProductType type) {
		this();
		this.code = code;
		this.naam = naam;
		this.prijs = prijs;
		this.type = type;
	}

	public Product() {
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public BigDecimal getPrijs() {
		return prijs;
	}

	public void setPrijs(BigDecimal prijs) {
		this.prijs = prijs;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (code != other.code)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [code=" + code + "]";
	}
	
	
}
