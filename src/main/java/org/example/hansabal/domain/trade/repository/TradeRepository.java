package org.example.hansabal.domain.trade.repository;


import org.example.hansabal.domain.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

	Page<Trade> findAllByTitleContainingOrderByTradeIdDesc(Pageable pageable,String title);

	@EntityGraph(attributePaths="users")
	@Query(value="SELECT t FROM Trade t WHERE t.trader.id=:traderId And t.deletedAt IS null ORDER BY t.tradeId asc",
		countQuery ="SELECT t FROM Trade t WHERE t.trader.id=:traderId")
	Page<Trade> findByTraderOrderByTradeIdAsc(@Param("traderId")Long traderId, Pageable pageable);
}
