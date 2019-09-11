package be.klavertjevier.kassa.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import be.klavertjevier.kassa.domain.Product.ProductType;

public interface OrderRepositoryCustom {

	Map<ProductType, Map<String, Integer>> totalenPerProduct(LocalDate datum);
	
	List<LocalDate> getOrderDatums();
}
