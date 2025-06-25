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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> , TradeRepositoryCustom{
	//
	// @EntityGraph(attributePaths="trader")
	// @Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null AND t.title like concat('%',:title,'%') Trade BY t.id Desc",
	// 	countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.title like concat('%',:title,'%') AND t.deletedAt IS null")
	// Page<Trade> findByTitleContainingAndDeletedAtIsNullOrderByIdDesc(@Param("title") String title, Pageable pageable);
	//
	// @EntityGraph(attributePaths="trader")
	// @Query(value="SELECT t FROM Trade t WHERE t.deletedAt IS null And t.trader.id=:traderId Trade BY t.id desc",
	// 	countQuery= "SELECT COUNT(t) FROM Trade t WHERE t.trader.id=:traderId And t.deletedAt IS null")
	// Page<Trade> findByTraderOrderByTradeIdDesc(@Param("traderId")Long traderId, Pageable pageable);

     List<Trade> findByUser(User user);

    @Query("SELECT o FROM Trade o WHERE o.product.id IN :ProductIds")
     List<Trade> findByProductIds(@Param("ProductIds") List<Long> ProductIds);

    @Query("""
		    SELECT o
		    FROM Trade o
		    WHERE o.status = :status
		      AND o.createdAt >= :from
		      AND o.createdAt < :to
		""")
    List<Trade> findAllByStatusAndCreatedDateRange(
            @Param("status") TradeStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("SELECT SUM(o.totalPrice) "
            + "FROM Trade o "
            + "WHERE o.user.id = :userId")
    Long findTotalPriceByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Trade o SET o.deletedAt = CURRENT_TIMESTAMP WHERE o.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    Page<Trade> findByWriter(User writer, Pageable attr0);

    Page<Trade> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
