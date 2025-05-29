package org.example.hansabal.domain.trade.repository;


import org.example.hansabal.domain.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
}
