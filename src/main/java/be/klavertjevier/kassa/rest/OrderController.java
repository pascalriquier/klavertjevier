package be.klavertjevier.kassa.rest;

import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import be.klavertjevier.kassa.domain.BetaalWijze;
import be.klavertjevier.kassa.domain.KlantRepository;
import be.klavertjevier.kassa.domain.Order;
import be.klavertjevier.kassa.domain.OrderLijn;
import be.klavertjevier.kassa.domain.OrderRepository;
import be.klavertjevier.kassa.domain.Product;
import be.klavertjevier.kassa.domain.Product.ProductType;
import be.klavertjevier.kassa.domain.ProductRepository;

@RestController
public class OrderController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private KlantRepository klantRepository;

	@PostMapping("/rest/order")
	public Order saveOrder(@RequestBody OrderDTO orderDTO) {
		Order order = new Order();
		List<OrderLijn> orderLijnen = orderDTO.producten.entrySet().stream().map(this::toOrderLijn)
				.collect(Collectors.toList());
		order.setOrderLijnen(orderLijnen);
		order.setKlant(klantRepository.findById(orderDTO.getKlant()).orElse(null));
		return orderRepository.save(order);
	}

	@PostMapping("/rest/orders/verwerkt")
	public void verwerkOrders(@RequestBody OrdersDTO ordersDTO) {
		ordersDTO.getIds().forEach(id -> {
			Order order = orderRepository.findById(id).orElse(null);
			order.setBetaald(ordersDTO.getBetaalWijze());
			order.setVerwerkt(true);
			orderRepository.save(order);
		});
	}

	@PostMapping("/rest/orders/betaald")
	public void betaalOrders(@RequestBody OrdersDTO ordersDTO) {
		ordersDTO.getIds().forEach(id -> {
			Order order = orderRepository.findById(id).orElse(null);
			order.setBetaald(ordersDTO.getBetaalWijze());
			orderRepository.save(order);
		});
	}

	@PostMapping("/rest/orders/geannuleerd")
	public void betaalOrders(@RequestBody Integer id) {
		Order order = orderRepository.findById(id).orElse(null);
		order.setGeannuleerd(true);
		orderRepository.save(order);
	}

	@DeleteMapping("/rest/orders")
	public void deleteOrders() {
		orderRepository.deleteAll();
	}

	@DeleteMapping(value = "/rest/order/{id}")
	public void deleteOrder(@PathVariable Integer id) {
		orderRepository.delete(orderRepository.findById(id).orElse(null));
	}

	@GetMapping(value = "/rest/order/{id}")
	public Order findOrder(@PathVariable Integer id) {
		return orderRepository.findById(id).orElse(null);
	}
	
	@RequestMapping("/rest/orders/totalenperproduct")
	public Map<ProductType, Map<Integer, Integer>> totalenPerProduct(
			@RequestParam(value = "datum", required = false) String datum) {
		return orderRepository.totalenPerProduct(datum == null || datum.trim().equals("") ? null
				: LocalDate.parse(datum, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

	@RequestMapping("/rest/orders/exporteer")
	public void exporteer(@RequestParam(value = "datum", required = false) String datum, HttpServletResponse response)
			throws IOException {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("Content-Disposition", "attachment;filename=\"overzicht.xlsx\"");
		response.setContentType("application/octet-stream");
		Map<ProductType, Map<Integer, Integer>> totalenPerProduct = orderRepository
				.totalenPerProduct(datum == null || datum.trim().equals("") ? null
						: LocalDate.parse(datum, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		List<Product> producten = StreamSupport.stream(productRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparing(Product::getType).thenComparing(Product::getCode))
				.collect(Collectors.toList());
		List<LocalDate> orderDatums = orderRepository.getOrderDatums();
		
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Overzicht");
			XSSFRow headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("Type");
			headerRow.createCell(1).setCellValue("Code");
			headerRow.createCell(2).setCellValue("Naam");
			headerRow.createCell(3).setCellValue("Eenheidsprijs");
			headerRow.createCell(4).setCellValue("Aantal verkocht");
			producten.stream().forEach((product) -> {
				XSSFRow row = sheet.createRow(producten.indexOf(product) + 1);
				row.createCell(0).setCellValue(product.getType().name());
				row.createCell(1).setCellValue(product.getCode());
				row.createCell(2).setCellValue(product.getNaam());
				row.createCell(3).setCellValue(product.getPrijs().setScale(2, RoundingMode.HALF_UP).doubleValue());
				row.createCell(4).setCellValue(totalenPerProduct.getOrDefault(product.getType(), new HashMap<Integer, Integer>()).getOrDefault(product.getCode(), 0));
			});
			
			AtomicInteger counter = new AtomicInteger(5);
			orderDatums.forEach(orderDatum -> {
				Stream.of(BetaalWijze.values()).forEach(betaalWijze -> {
					int column = counter.getAndIncrement();
					Map<Integer, Integer> totalenPerDatumEnBetaalwijze = orderRepository.totalenPerProduct(orderDatum, betaalWijze);
					sheet.getRow(0).createCell(column).setCellValue(orderDatum + " - " + betaalWijze);
					producten.stream().forEach(product -> {
						XSSFRow row = sheet.getRow(producten.indexOf(product) + 1);
						row.createCell(column).setCellValue(totalenPerDatumEnBetaalwijze.getOrDefault(product.getCode(), 0));
					});
				});
			});
			
			workbook.write(response.getOutputStream());
		}
	}

	@RequestMapping("/rest/orders/totalen")
	public Integer ordersPerDag(@RequestParam(value = "datum", required = false) String datum) {
		return orderRepository.ordersPerDag(datum == null || datum.trim().equals("") ? null
				: LocalDate.parse(datum, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

	@RequestMapping("/rest/orders/datums")
	public List<LocalDate> orderDatums() {
		return orderRepository.getOrderDatums();
	}

	private OrderLijn toOrderLijn(Entry<Integer, Integer> e) {
		return new OrderLijn(productRepository.findById(e.getKey()).orElseThrow(() -> new IllegalArgumentException()),
				e.getValue());
	}

	public static class OrdersDTO {
		private BetaalWijze betaalWijze;
		private List<Integer> ids;

		public List<Integer> getIds() {
			return ids;
		}

		public void setIds(List<Integer> ids) {
			this.ids = ids;
		}

		public void setBetaalWijze(BetaalWijze betaalWijze) {
			this.betaalWijze = betaalWijze;
		}
		
		public BetaalWijze getBetaalWijze() {
			return betaalWijze;
		}
	}

	public static class OrderDTO {
		private Map<Integer, Integer> producten;
		private Integer klant;

		public Map<Integer, Integer> getProducten() {
			return producten;
		}

		public void setProducten(Map<Integer, Integer> producten) {
			this.producten = producten;
		}

		public Integer getKlant() {
			return klant;
		}

		public void setKlant(Integer klant) {
			this.klant = klant;
		}

	}

}
