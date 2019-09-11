package be.klavertjevier.kassa.domain;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import be.klavertjevier.kassa.domain.Product.ProductType;


@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Map<ProductType, Map<String, Integer>> totalenPerProduct(LocalDate datum) {
		String whereClause = datum == null ? "" : "WHERE o.datum = :datum";
		String totalenQuery = "SELECT product_code code, sum(quantity) som FROM Order_Order_Lijnen ol join Orders o on ol.order_id = o.id " + whereClause + " group by product_code";
		Query query = entityManager.createNativeQuery("select p.naam, p.type, totals.som from product p join (" + totalenQuery
				+ ") totals on p.code = totals.code");
		if (datum != null) {
			query.setParameter("datum", datum);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		List<ProductTotaal> totalen = result.stream().map(this::toProductTotaal).collect(Collectors.toList());
		return	totalen.stream().collect(Collectors.groupingBy(ProductTotaal::getProductType, Collectors.toMap(ProductTotaal::getNaam, ProductTotaal::getTotaal)));
	}
	
	@Override
	public List<LocalDate> getOrderDatums() {
		return entityManager.createQuery("SELECT distinct(o.datum) from Order o", LocalDate.class).getResultList();
	}

	
	private ProductTotaal toProductTotaal(Object[] result) {
		return new ProductTotaal((String) result[0], ProductType.valueOf((String) result[1]), 
				((BigInteger) result[2]).intValue());
	}
	
	private static class ProductTotaal {
		private String naam;
		private ProductType productType;
		private int totaal;
		
		public ProductTotaal(String naam, ProductType productType, int totaal) {
			super();
			this.naam = naam;
			this.productType = productType;
			this.totaal = totaal;
		}
		
		public String getNaam() {
			return naam;
		}
		
		public ProductType getProductType() {
			return productType;
		}
		public int getTotaal() {
			return totaal;
		}
		
		
	}


}
