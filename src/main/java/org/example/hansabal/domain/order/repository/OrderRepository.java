package org.example.hansabal.domain.order.repository;


import org.example.hansabal.domain.order.entity.Order;
import org.example.hansabal.domain.order.entity.OrderStatus;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUser(User user);

	@Query("SELECT o FROM Order o WHERE o.Product.id IN :ProductIds")
	List<Order> findByProductIds(@Param("ProductIds") List<Long> ProductIds);

	@Query("""
		    SELECT o
		    FROM Order o
		    WHERE o.status = :status
		      AND o.createdDate >= :from
		      AND o.createdDate < :to
		""")
	List<Order> findAllByStatusAndCreatedDateRange(
		@Param("status") OrderStatus status,
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to
	);

	@Query("SELECT SUM(o.totalPrice) "
		+ "FROM Order o "
		+ "WHERE o.user.id = :userId")
	Long findTotalPriceByUserId(@Param("userId") Long userId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Order o SET o.deletedDate = CURRENT_TIMESTAMP WHERE o.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
}
