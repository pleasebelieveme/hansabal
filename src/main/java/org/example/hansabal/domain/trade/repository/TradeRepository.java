package org.example.hansabal.domain.trade.repository;


import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.entity.TradeStatus;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> , TradeRepositoryCustom{
	//
	// @EntityGraph(attributePaths="trader")
	// @Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null AND t.title like concat('%',:title,'%') ORDER BY t.id Desc",
	// 	countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.title like concat('%',:title,'%') AND t.deletedAt IS null")
	// Page<Trade> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(@Param("title") String title, Pageable pageable);
	//
	// @EntityGraph(attributePaths="trader")
	// @Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null And t.trader.id=:traderId ORDER BY t.id desc",
	// 	countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.trader.id=:traderId And t.deletedAt IS null")
	// Page<Trade> findByTraderOrderByTradeIdDesc(@Param("traderId")Long traderId, Pageable pageable);

	@Query("SELECT t FROM Trade t WHERE t.id = :id AND t.deletedAt IS NULL")
	Optional<Trade> findByIdAndNotDeleted(@Param("id") Long id);

}
