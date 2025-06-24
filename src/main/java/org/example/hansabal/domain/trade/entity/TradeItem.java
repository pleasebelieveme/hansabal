package org.example.hansabal.domain.trade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.hansabal.domain.product.entity.Product;

@Getter
@Entity
@Table(name = "Trade_items")
public class TradeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public TradeItem(int quantity, Trade trade, Product product ){
        this.quantity = quantity;
        this.trade = trade;
        this.product = product;
    }

    public TradeItem() {

    }
}
