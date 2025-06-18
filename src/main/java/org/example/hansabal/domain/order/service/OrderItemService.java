package org.example.hansabal.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.order.entity.Order;
import org.example.hansabal.domain.order.entity.OrderItem;
import org.example.hansabal.domain.order.exception.OrderErrorCode;
import org.example.hansabal.domain.order.repository.OrderItemRepository;
import org.example.hansabal.domain.order.repository.OrderRepository;
import org.example.hansabal.domain.product.dto.response.CartItemResponse;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.product.exception.ProductErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

	private final OrderItemRepository orderItemRepository;
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	public List<Long> addOrderItem(Long orderId, List<CartItemResponse> items) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new BizException(OrderErrorCode.ORDER_NOT_FOUND));

		List<Long> addedIds = new ArrayList<>();

		for (CartItemResponse item : items) {

			Product product = productRepository.findById(item.ProductId())
					.orElseThrow(() -> new BizException(ProductErrorCode.PRODUCT_NOT_FOUND));

			OrderItem orderItem = new OrderItem(item.quantity(), order, product);

			OrderItem savedOrderItem = orderItemRepository.save(orderItem);
			addedIds.add(savedOrderItem.getId());
		}
		return addedIds;
	}

	public List<Long> getOrderItemIds(Long orderId) {
		return orderItemRepository.findIdsByOrderId(orderId);
	}
}
