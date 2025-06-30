package org.example.hansabal.domain.wallet.repository;

import java.util.Optional;

import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	@Query("SELECT w FROM Wallet w join fetch w.user WHERE w.user=:user And w.deletedAt=Null")
	Optional<Wallet> findByUser(@Param("user") User user);

	boolean existsByUser(User user);
}
