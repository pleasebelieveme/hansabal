package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.dto.response.RequestsResponseDto;
import org.example.hansabal.domain.trade.entity.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> , RequestsRepositoryCustom{
	// @EntityGraph(attributePaths = "trade")
	// @Query(value="SELECT r FROM Requests r WHERE r.deletedAt IS null AND r.trade.id=:tradeId  ORDER BY r.id asc",
	// 	countQuery= "SELECT COUNT(r) FROM Requests r WHERE r.trade.id=:tradeId AND r.deletedAt IS null")
	// Page<RequestsResponseDto> findByTradeIdOrderByRequestsIdAsc(@Param("tradeId")Long tradeId,Pageable pageable);
}
