package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.entity.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepository extends JpaRepository<Requests, Long> {
}
