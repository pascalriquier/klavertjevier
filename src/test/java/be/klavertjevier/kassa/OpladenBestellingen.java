package be.klavertjevier.kassa;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import be.klavertjevier.kassa.domain.Klant;
import be.klavertjevier.kassa.domain.Order;
import be.klavertjevier.kassa.domain.Product;
import be.klavertjevier.kassa.rest.OrderController.OrderDTO;
import be.klavertjevier.kassa.rest.OrderController.OrdersDTO;

public class OpladenBestellingen {

	public static void main(String[] args) throws IOException {
		List<Product> producten = Stream.of(new RestTemplate().getForEntity("http://localhost:8080/rest/products", Product[].class).getBody()).collect(Collectors.toList());
		ClassPathResource bestand = new ClassPathResource("/Inschrijving eetdagen Klavertje Vier (Antwoorden) - Inschrijvingen.csv");
		try (CSVParser csvParser = new CSVParser(new InputStreamReader(bestand.getInputStream(), StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
			csvParser.getRecords().forEach(record -> maakBestellingAan(record, producten));
		}
	}
	
//0 Tijdstempel,
//1	Naam + voornaam,
//2	Aantal personen,
//3	Tijdslot,
//4	Soep (€ 5) ,
//5	Kaaskroketten - 2 stuks (€ 9) ,
//6	Garnaalkroketten - 2 stuks (€ 13) ,
//7	Duo kaas- en garnaalkroket (€ 11) ,
//8	,
//9	Vol-au-vent (€ 15) ,
//10	"Mosselen natuur - 1,2 kg (€ 22) ",
//11	"Mosselen witte wijn - 1,2 kg (€ 24) ",
//12	"Mosselen K4 special - 1,2 kg (€ 24) ",
//13	Groentenburger (€ 12) ,
//14	Mosselen natuur - 700g (€ 15) ,
//15	Vol-au-vent (€ 10) ,
//16	Curryworsten - 2 stuks (€ 8) ,
//17	Kipnuggets - 5 stuks (€ 8) ,
//18	Email,
//19	Totaal bedrag,
//20	Klantnummer,
//21	Email verzonden,
//22	Betaling

	private static void maakBestellingAan(CSVRecord record, List<Product> producten) {
		Map<Integer, Integer> productCodeMapping = new HashMap<Integer, Integer>();
		productCodeMapping.put(4, 1);
		productCodeMapping.put(5, 2);
		productCodeMapping.put(6, 3);
		productCodeMapping.put(7, 4);
		productCodeMapping.put(8, Integer.MIN_VALUE);
		productCodeMapping.put(9, 5);
		productCodeMapping.put(10, 6);
		productCodeMapping.put(11, 7);
		productCodeMapping.put(12, 8);
		productCodeMapping.put(13, 9);
		productCodeMapping.put(14, 11);
		productCodeMapping.put(15, 10);
		productCodeMapping.put(16, 12);
		productCodeMapping.put(17, 13);
		
		if (record.getRecordNumber() > 1) {
			Map<Integer, Integer> orderProducten = new HashMap<Integer, Integer>();
			for (int index = 4; index <= 17; index++) {
				if (!isEmpty(record.get(index))) {
					int aantal = Integer.parseInt(record.get(index));
					if (aantal > 0) {
						int productCode = productCodeMapping.get(index);
						Product product = producten.stream().filter(p -> p.getCode() == productCode).findFirst().orElseThrow(() -> new IllegalArgumentException("kan niet"));
						orderProducten.put(product.getCode(), aantal);
					}
				}
			}
			System.out.println("Order voor " + record.get(1) + ", " + orderProducten.size() + " producten");
			if (!orderProducten.isEmpty()) {
				Klant klant = new Klant();
				klant.setNaam(record.get(1));
				klant.setNummer(record.get(20));
				
				Klant aangemaakteKlant = new RestTemplate().postForEntity("http://localhost:8080/rest/klant", klant, Klant.class).getBody();
				
				OrderDTO order = new OrderDTO();
				order.setProducten(orderProducten);
				order.setKlant(aangemaakteKlant.getId());
				Order aangemaaktOrder = new RestTemplate().postForEntity("http://localhost:8080/rest/order", order, Order.class).getBody();
				
				if (record.get(22).equals("OK")) {
					OrdersDTO ordersBetaald = new OrdersDTO();
					ordersBetaald.setIds(java.util.Arrays.asList(aangemaaktOrder.getId()));
					new RestTemplate().postForEntity("http://localhost:8080/rest/orders/betaald", ordersBetaald, String.class);
				}
			}
		}
	}
}
