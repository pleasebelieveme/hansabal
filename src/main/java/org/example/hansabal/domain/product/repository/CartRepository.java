package org.example.hansabal.domain.product.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.hansabal.domain.product.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Modifying(clearAutomatically = true)
    void deleteAllByProductId(Long productId);

    @EntityGraph(attributePaths = {"menu", "menuOption"})
    List<Cart> findByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Cart c SET c.deletedAt = current_timestamp WHERE c.user.id = :userId")
        //@Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    void deleteAllById(Long cartId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.updatedAt <= :expiredTime")
    void deleteAllExpired(@Param("expiredTime") LocalDateTime expiredTime);

    @EntityGraph(attributePaths = {"user"})
    Optional<Cart> findById(Long cartId);

    @Modifying(clearAutomatically = true)
    void deleteById(Long cartId);

}
