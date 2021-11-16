package br.com.telefonica.producer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import br.com.telefonica.producer.model.Order;

public interface OrderRepository extends CrudRepository<Order,Long> {

    List<Order> findByTotalBetween(Double minTotal, Double maxTotal);

    @Query("SELECT o FROM Order o WHERE (o.name like %:query% or o.description like %:query%) and o.total >= :minPrice and o.total <= :maxPrice") 
    List<Order> findByNameOrDescriptionContainsAndTotalBetween(String query, Double minPrice, Double maxPrice);

    
}
