package org.example.hansabal.domain.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.hansabal.domain.trade.entity.TradeItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {

	@Query("SELECT oi.id FROM TradeItem oi WHERE oi.trade.id = :TradeId")
	List<Long> findIdsByTradeId(@Param("TradeId") Long TradeId);

	List<TradeItem> findByTradeId(Long TradeId);
}
