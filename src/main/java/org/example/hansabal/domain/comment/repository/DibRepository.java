package org.example.hansabal.domain.comment.repository;

import java.util.Optional;

import org.example.hansabal.domain.comment.entity.Dib;
import org.example.hansabal.domain.comment.entity.DibType;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DibRepository extends JpaRepository<Dib,Long> {
	Optional<Dib> findByUserAndDibTypeAndTargetId(User user, DibType dibType, Long targetId);
}
