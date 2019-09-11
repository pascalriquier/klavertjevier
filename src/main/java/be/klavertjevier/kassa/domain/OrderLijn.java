package be.klavertjevier.kassa.domain;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class OrderLijn {
	@ManyToOne
	private Product product;
	private int quantity;

	public OrderLijn(Product product, int quantity) {
		this();
		this.product = product;
		this.quantity = quantity;
	}

	public OrderLijn() {
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
