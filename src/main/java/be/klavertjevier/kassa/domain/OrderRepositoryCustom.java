package be.klavertjevier.kassa.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import be.klavertjevier.kassa.domain.Product.ProductType;

public interface OrderRepositoryCustom {

	Map<ProductType, Map<Integer, Integer>> totalenPerProduct(LocalDate datum);

	Map<Integer, Integer> totalenPerProduct(LocalDate datum, BetaalWijze betaalWijze);
	
	Integer ordersPerDag(LocalDate datum);
	
	List<LocalDate> getOrderDatums();
}
