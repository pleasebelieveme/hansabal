package org.example.hansabal.domain.trade.repository;

import org.example.hansabal.domain.trade.entity.Requestes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestesRepository extends JpaRepository<Requestes, Long> {
}
