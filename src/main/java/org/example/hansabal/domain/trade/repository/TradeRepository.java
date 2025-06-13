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

	@EntityGraph(attributePaths="trader")
	@Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null AND t.title like concat('%',:title,'%') ORDER BY t.id Desc",
		countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.title like concat('%',:title,'%') AND t.deletedAt IS null")
	Page<Trade> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(@Param("title") String title, Pageable pageable);

	@EntityGraph(attributePaths="trader")
	@Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null And t.trader.id=:traderId ORDER BY t.id desc",
		countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.trader.id=:traderId And t.deletedAt IS null")
	Page<Trade> findByTraderOrderByTradeIdDesc(@Param("traderId")Long traderId, Pageable pageable);
}
