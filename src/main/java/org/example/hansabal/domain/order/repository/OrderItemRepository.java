package org.example.hansabal.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.hansabal.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Query("SELECT oi.id FROM OrderItem oi WHERE oi.order.id = :orderId")
	List<Long> findIdsByOrderId(@Param("orderId") Long orderId);

	List<OrderItem> findByOrderId(Long orderId);
}
