package be.klavertjevier.kassa.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import be.klavertjevier.kassa.domain.Order;
import be.klavertjevier.kassa.domain.OrderLijn;
import be.klavertjevier.kassa.domain.OrderRepository;
import be.klavertjevier.kassa.domain.Product.ProductType;
import be.klavertjevier.kassa.domain.ProductRepository;

@RestController
public class OrderController {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@PostMapping("/rest/order")
	public void saveOrder(@RequestBody OrderDTO orderDTO) {
		Order order = new Order();
		order.setKlant(orderDTO.getKlant());
		order.setVoorschot(orderDTO.getVoorschot());
		List<OrderLijn> orderLijnen = orderDTO.producten.entrySet().stream().map(this::toOrderLijn).collect(Collectors.toList());
		order.setOrderLijnen(orderLijnen);
		orderRepository.save(order);
	}
	
	@DeleteMapping("/rest/orders")
	public void deleteOrders() {
		orderRepository.deleteAll();
	}
	
	@RequestMapping("/rest/orders/totalenperproduct")
	public Map<ProductType, Map<String, Integer>> totalenPerProduct(@RequestParam(value = "datum", required = false) String datum) {
		return orderRepository.totalenPerProduct(datum == null || datum.trim().equals("") ? null : LocalDate.parse(datum, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

	@RequestMapping("/rest/orders/datums")
	public List<LocalDate> orderDatums() {
		return orderRepository.getOrderDatums();
	}

	private OrderLijn toOrderLijn(Entry<Integer, Integer> e) {
		return new OrderLijn(productRepository.findById(e.getKey()).orElseThrow(() -> new IllegalArgumentException()), e.getValue());
	}
	
	public static class OrderDTO {
		private String klant;
		private BigDecimal voorschot;
		private Map<Integer, Integer> producten;

		public Map<Integer, Integer> getProducten() {
			return producten;
		}

		public void setProducten(Map<Integer, Integer> producten) {
			this.producten = producten;
		}

		public String getKlant() {
			return klant;
		}

		public void setKlant(String klant) {
			this.klant = klant;
		}

		public BigDecimal getVoorschot() {
			return voorschot;
		}

		public void setVoorschot(BigDecimal voorschot) {
			this.voorschot = voorschot;
		}
	}
	
}
