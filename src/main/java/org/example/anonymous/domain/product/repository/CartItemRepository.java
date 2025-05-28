package org.example.anonymous.domain.product.repository;

import org.example.anonymous.domain.product.entity.Cart;
import org.example.anonymous.domain.product.entity.CartItem;
import org.example.anonymous.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT i FROM CartItem i JOIN FETCH i.product JOIN FETCH i.cart "
            + "WHERE i.cart =:cart AND i.product = :product")
    Optional<CartItem> findByCartAndProduct(@Param("cart") Cart cart, @Param("product") Product product);

    @Query("SELECT i FROM CartItem i JOIN FETCH i.product WHERE i.cart.id = :cartId")
    List<CartItem> findAllByCart(@Param("cartId") Long cartId);

    @Modifying
    @Query("DELETE FROM CartItem i WHERE i.cart.id = :cartId")
    void deleteAllByCartId(Long cartId);
}
