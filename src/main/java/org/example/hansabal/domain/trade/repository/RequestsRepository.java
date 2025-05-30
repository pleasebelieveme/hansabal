package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.entity.Requests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> {
	@Query(value="SELECT r FROM Requests r join fetch r.trade t WHERE t.tradeId=:tradeId",
	countQuery ="SELECT r FROM Requests r WHERE r.trade.tradeId=:tradeId")
	Page<Requests> findAllByTradeIdOrderByRequestsIdDesc(Long tradeId,Pageable pageable);
}
