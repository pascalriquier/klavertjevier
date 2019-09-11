package be.klavertjevier.kassa.domain;


import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<Order, Integer>, OrderRepositoryCustom {
}
