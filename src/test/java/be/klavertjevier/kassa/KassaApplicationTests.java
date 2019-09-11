package be.klavertjevier.kassa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import be.klavertjevier.kassa.domain.Order;
import be.klavertjevier.kassa.domain.OrderLijn;
import be.klavertjevier.kassa.domain.OrderRepository;
import be.klavertjevier.kassa.domain.Product;
import be.klavertjevier.kassa.domain.ProductRepository;
import be.klavertjevier.kassa.domain.Product.ProductType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KassaApplicationTests {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@After
	public void tearDown() {
		orderRepository.findAll().forEach(orderRepository::delete);
		productRepository.findAll().forEach(productRepository::delete);
	}
	
	@Test
	public void totalenPerProductWerkt() {
		Product p1 = productRepository.save(new Product(1, "p1", new BigDecimal("2.20"), ProductType.DRANK));
		Product p2 = productRepository.save(new Product(2, "p2", new BigDecimal("2.20"), ProductType.DRANK));
		Product p3 = productRepository.save(new Product(3, "p3", new BigDecimal("2.20"), ProductType.HOOFDGERECHT));
		
		orderRepository.save(new Order("klant", Arrays.asList(new OrderLijn(p1, 2), new OrderLijn(p2, 1))));
		orderRepository.save(new Order("klant2", Arrays.asList(new OrderLijn(p1, 2), new OrderLijn(p2, 2), new OrderLijn(p3, 5))));
		
		Map<ProductType, Map<String, Integer>> totalenPerProduct = orderRepository.totalenPerProduct(null);
		assertThat(totalenPerProduct).hasSize(2);
		assertThat(totalenPerProduct.get(ProductType.DRANK)).hasSize(2);
		assertThat(totalenPerProduct.get(ProductType.DRANK).get("p1")).isEqualTo(4);
		assertThat(totalenPerProduct.get(ProductType.DRANK).get("p2")).isEqualTo(3);
		assertThat(totalenPerProduct.get(ProductType.HOOFDGERECHT)).hasSize(1);
		assertThat(totalenPerProduct.get(ProductType.HOOFDGERECHT).get("p3")).isEqualTo(5);
	}

	@Test
	public void totalenPerProductWerktMetDatum() {
		Product p1 = productRepository.save(new Product(1, "p1", new BigDecimal("2.20"), ProductType.DRANK));
		Product p2 = productRepository.save(new Product(2, "p2", new BigDecimal("2.20"), ProductType.DRANK));
		Product p3 = productRepository.save(new Product(3, "p3", new BigDecimal("2.20"), ProductType.HOOFDGERECHT));
		
		orderRepository.save(new Order("klant", Arrays.asList(new OrderLijn(p1, 2), new OrderLijn(p2, 1))));
		orderRepository.save(new Order("klant2", Arrays.asList(new OrderLijn(p1, 2), new OrderLijn(p2, 2), new OrderLijn(p3, 5))));
		orderRepository.save(new Order(LocalDate.now().minusDays(1), "klant2", Arrays.asList(new OrderLijn(p1, 2), new OrderLijn(p2, 2), new OrderLijn(p3, 5))));
		
		Map<ProductType, Map<String, Integer>> totalenPerProduct = orderRepository.totalenPerProduct(LocalDate.now());
		assertThat(totalenPerProduct).hasSize(2);
		assertThat(totalenPerProduct.get(ProductType.DRANK)).hasSize(2);
		assertThat(totalenPerProduct.get(ProductType.DRANK).get("p1")).isEqualTo(4);
		assertThat(totalenPerProduct.get(ProductType.DRANK).get("p2")).isEqualTo(3);
		assertThat(totalenPerProduct.get(ProductType.HOOFDGERECHT)).hasSize(1);
		assertThat(totalenPerProduct.get(ProductType.HOOFDGERECHT).get("p3")).isEqualTo(5);
	}

}
