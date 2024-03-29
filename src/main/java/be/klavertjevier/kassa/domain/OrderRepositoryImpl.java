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
	public Map<ProductType, Map<Integer, Integer>> totalenPerProduct(LocalDate dag) {
		String whereClause = "WHERE o.geannuleerd = false";
		if (dag != null) {
			whereClause += " and o.dag = :dag";
		}
		String totalenQuery = "SELECT product_code code, sum(quantity) som FROM Order_Order_Lijnen ol join Orders o on ol.order_id = o.id " + whereClause + " group by product_code";
		Query query = entityManager.createNativeQuery("select p.code, p.type, totals.som from product p join (" + totalenQuery
				+ ") totals on p.code = totals.code");
		if (dag != null) {
			query.setParameter("dag", dag);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		List<ProductTotaal> totalen = result.stream().map(this::toProductTotaal).collect(Collectors.toList());
		return	totalen.stream().collect(Collectors.groupingBy(ProductTotaal::getProductType, Collectors.toMap(ProductTotaal::getCode, ProductTotaal::getTotaal)));
	}
	
	@Override
	public List<LocalDate> getOrderDatums() {
		return entityManager.createQuery("SELECT distinct(o.dag) from Order o", LocalDate.class).getResultList();
	}

	
	private ProductTotaal toProductTotaal(Object[] result) {
		return new ProductTotaal((int) result[0], ProductType.valueOf((String) result[1]), 
				((BigInteger) result[2]).intValue());
	}
	
	private static class ProductTotaal {
		private Integer code;
		private ProductType productType;
		private int totaal;
		
		public ProductTotaal(int code, ProductType productType, int totaal) {
			super();
			this.code = code;
			this.productType = productType;
			this.totaal = totaal;
		}

		public int getCode() {
			return code;
		}
		
		public ProductType getProductType() {
			return productType;
		}
		public int getTotaal() {
			return totaal;
		}
		
		
	}

	@Override
	public Integer ordersPerDag(LocalDate dag) {
		String whereClause = dag == null ? " WHERE o.geannuleerd = false" : " WHERE o.dag = :dag and o.geannuleerd = false";
		String totalenQuery = "SELECT count(*) FROM Orders o" + whereClause;
		Query query = entityManager.createNativeQuery(totalenQuery);
		if (dag != null) {
			query.setParameter("dag", dag);
		}
		return ((BigInteger) query.getSingleResult()).intValue();
	}

	@Override
	public Map<Integer, Integer> totalenPerProduct(LocalDate datum, BetaalWijze betaalWijze) {
		String whereClause = "WHERE o.dag = :dag and o.geannuleerd = false and o.betaald_met = :betaalWijze";
		String totalenQuery = "SELECT product_code code, sum(quantity) som FROM Order_Order_Lijnen ol join Orders o on ol.order_id = o.id " + whereClause + " group by product_code";
		Query query = entityManager.createNativeQuery(totalenQuery);
		query.setParameter("dag", datum);
		query.setParameter("betaalWijze", betaalWijze.name());
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		return result.stream()
				.collect(Collectors.toMap(r -> (int) r[0], r -> ((BigInteger) r[1]).intValue()));
	}


}
