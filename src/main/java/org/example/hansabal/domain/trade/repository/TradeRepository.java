package org.example.hansabal.domain.trade.repository;


import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

	Page<Trade> findAllByOrderByTradeIdDesc(Pageable pageable);

	@EntityGraph(attributePaths="users")
	@Query(value="SELECT t FROM Trade t WHERE t.trader.id=:trader And t.deletedAt=null ORDER BY t.tradeId asc",
		countQuery ="SELECT t FROM Trade t WHERE t.trader.id=:trader")
	Page<Trade> findByTraderOrderByTradeIdDesc(User trader, Pageable pageable);
}
