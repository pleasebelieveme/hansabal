package org.example.hansabal.domain.order.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.order.dto.request.OrderStatusRequestDto;
import org.example.hansabal.domain.order.entity.Order;
import org.example.hansabal.domain.order.entity.OrderItem;
import org.example.hansabal.domain.order.entity.OrderStatus;
import org.example.hansabal.domain.order.exception.OrderErrorCode;
import org.example.hansabal.domain.order.repository.OrderItemRepository;
import org.example.hansabal.domain.order.repository.OrderRepository;
import org.example.hansabal.domain.order.response.MenuOptionDetailResponseDto;
import org.example.hansabal.domain.order.response.OrderDetailResponseDto;
import org.example.hansabal.domain.order.response.OrderItemDetailResponseDto;
import org.example.hansabal.domain.order.response.OrderResponseDto;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.users.exception.UserErrorCode;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	/*
	private final CartService cartService;
	private final CouponService couponService;
	private final PointHistoryService pointHistoryService;
	*/
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderItemService orderItemService;
	private final OrderItemRepository orderItemRepository;

	/**
	 * 주문 생성 로직
	 * - 쿠폰 적용
	 * - 포인트 차감
	 * - 총 금액 계산
	 * - 주문 저장 및 주문 항목 저장
	 * - 장바구니 제거
	 */
/*	@LogOrderCreation
	public OrderResponseDto createOrder(OrderRequestDto requestDto, UserAuth userAuth) {
		Coupon coupon = null;
		Long userId = userAuth.getId();

		// 쿠폰 유효성 검증
		if (requestDto.couponId() != null) {
			coupon = couponService.getCoupon(requestDto.couponId());
		}

		// 포인트 유효성 검증
		Integer remainingPoint = 0;
		if (requestDto.point() != null) {
			remainingPoint = pointHistoryService.getRemainingPoint(userId);
			if (remainingPoint == null) throw new BizException(PointErrorCode.NO_POINT);
			if (requestDto.point() > remainingPoint) throw new BizException(PointErrorCode.INSUFFICIENT_POINT);
		}

		// 장바구니 조회
		CartResponseDto cartResponseDto = cartService.findCarts(userAuth);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BizException(UserErrorCode.INVALID_ID));
		Product product = productRepository.findById(cartResponseDto.productId())
				.orElseThrow(() -> new BizException(ProductErrorCode.PRODUCT_NOT_FOUND));
		List<CartItemResponse> items = cartResponseDto.cartItems();

		int totalPrice = cartResponseDto.totalPrice();

		// 최소 주문 금액, 영업 시간 등 검증
		verifyProduct(product, totalPrice);

		// 쿠폰 할인 적용
		totalPrice = checkCoupon(totalPrice, coupon);

		// 포인트 사용
		if (totalPrice < requestDto.point()) {
			throw new BizException(PointErrorCode.EXCEEDING_POINT_AMOUNT);
		}
		if (requestDto.point() != null) {
			totalPrice -= requestDto.point();
		}

		// 배달비 추가
		totalPrice += product.getDeliveryFee();

		// 주문 저장
		Order order = new Order(totalPrice, user, product);
		Order savedOrder = orderRepository.save(order);

		// 주문 항목 저장
		List<Long> orderItemIds = orderItemService.addOrderItem(savedOrder.getId(), items);

		// 쿠폰 사용 처리
		if (coupon != null) {
			couponService.useCoupon(coupon);
		}

		// 포인트 차감 처리
		if (requestDto.point() != null) {
			pointHistoryService.usePoint(userId, requestDto.point());
		}

		// 장바구니 항목 삭제
		for (CartItemResponse item : items) {
			cartService.deleteCart(item.id(), userAuth);
		}

		return new OrderResponseDto(
				savedOrder.getId(),
				userId,
				cartResponseDto.productId(),
				orderItemIds,
				totalPrice,
				savedOrder.getStatus()
		);
	}

	/**
	 * 상품의 최소 주문 금액 및 영업 시간 체크
	 */
	/*
	private void verifyProduct(Product product, int totalPrice) {
		LocalTime now = LocalTime.now();

		if (totalPrice < product.getMinDeliveryPrice()) {
			throw new BizException(OrderErrorCode.UNDER_MINIMUM_ORDER_PRICE);
		}
		if (product.getOpenTime().isAfter(now) || product.getCloseTime().isBefore(now)) {
			throw new BizException(OrderErrorCode.Product_CLOSED);
		}
	}
*/
	/**
	 * 쿠폰 유형에 따라 총 금액 할인 적용
	 */
/*	private int checkCoupon(int totalPrice, Coupon coupon) {
		if (coupon == null) return totalPrice;

		if (coupon.getCouponType() == CouponType.A) {
			// 퍼센트 할인
			int discount = totalPrice * (100 - coupon.getDeductedPrice()) / 100;
			totalPrice -= Math.min(coupon.getMaxDiscount(), discount);
		} else if (coupon.getCouponType() == CouponType.B) {
			// 정액 할인
			if (coupon.getDeductedPrice() > totalPrice) {
				throw new BizException(CouponErrorCode.COUPON_EXCEEDS_TOTAL);
			}
			totalPrice -= coupon.getDeductedPrice();
		}

		return totalPrice;
	}
*/
	/**
	 * 사장님이 주문 상태를 변경하는 로직
	 * - CHECKING → COOKING → DELIVERING → FINISHED 순으로 상태 전이
	 * - 최종 완료 시 포인트 적립
	 */
	@Transactional
	public OrderResponseDto updateOrder(Long userId, Long orderId, @Valid OrderStatusRequestDto requestDto) {
		User user = validateUser(userId);
		Order order = validateOrder(user, orderId);

		if (!order.getStatus().equals(requestDto.orderStatus())) {
			throw new BizException(OrderErrorCode.INVALID_ORDER_STATUS);
		}

		if (order.getStatus() == OrderStatus.CHECKING) {
			order.updateStatus(OrderStatus.COOKING);
		} else if (order.getStatus() == OrderStatus.COOKING) {
			order.updateStatus(OrderStatus.DELIVERING);
		} else if (order.getStatus() == OrderStatus.DELIVERING) {
			order.updateStatus(OrderStatus.FINISHED);
			//pointHistoryService.getPoint(userId, order.getTotalPrice());
		}

		List<Long> menuIds = orderItemService.getOrderItemIds(orderId);

		return new OrderResponseDto(
				order.getId(),
				userId,
				order.getProduct().getId(),
				menuIds,
				order.getTotalPrice(),
				order.getStatus()
		);
	}

	/**
	 * 주문 소유자(사장) 확인 및 주문 상태 검증
	 */
	private Order validateOrder(User user, Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new BizException(OrderErrorCode.ORDER_NOT_FOUND));

		if (!Objects.equals(order.getProduct().getUser().getId(), user.getId())) {
			throw new BizException(OrderErrorCode.PRODUCT_OWNER_MISMATCH);
		}

		if (order.getStatus() == OrderStatus.FINISHED) {
			throw new BizException(OrderErrorCode.ORDER_ALREADY_FINISHED);
		}

		return order;
	}

	/**
	 * 사용자 유효성 검증
	 */
	private User validateUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new BizException(UserErrorCode.INVALID_REQUEST));
	}

	/**
	 * 주문 거절 처리 (CHECKING 상태에서만 가능)
	 */
	@Transactional
	public void refuseOrder(Long userId, Long orderId) {
		User user = validateUser(userId);
		Order order = validateOrder(user, orderId);

		if (!order.getStatus().equals(OrderStatus.CHECKING)) {
			throw new BizException(OrderErrorCode.INVALID_ORDER_REJECTION);
		}

		order.updateStatus(OrderStatus.REFUSED);
	}

	/**
	 * 사용자 역할에 따른 주문 전체 조회
	 * - 일반 사용자: 자신이 주문한 주문
	 * - 사장님: 자신이 등록한 가게의 주문
	 */
	public List<OrderResponseDto> findAllOrders(Long userId) {
		User user = validateUser(userId);
		List<Order> orders = new ArrayList<>();

		if (user.getUserRole().equals(UserRole.USER)) {
			orders = orderRepository.findByUser(user);
		} else if (user.getUserRole().equals(UserRole.ADMIN)) {
			List<Long> productIds = productRepository.findIdByUser(user);
			orders = orderRepository.findByProductIds(productIds);
		}

		return orders.stream()
				.map(order -> new OrderResponseDto(
						order.getId(),
						order.getUser().getId(),
						order.getProduct().getId(),
						orderItemService.getOrderItemIds(order.getId()),
						order.getTotalPrice(),
						order.getStatus()
				))
				.collect(Collectors.toList());
	}

	/**
	 * 특정 주문의 상세 정보 조회
	 * - 주문 항목(Menu, Option 등) 포함
	 * - 주문자가 직접 접근하거나 사장님이 접근 가능
	 */
	@Transactional
	public OrderDetailResponseDto findOrderDetails(Long userId, Long orderId) {
		User user = validateUser(userId);
		Order order = validateDetailOrder(user, orderId);

		List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

		List<OrderItemDetailResponseDto> orderItemDtos = orderItems.stream()
				.map(orderItem -> new OrderItemDetailResponseDto(
						orderItem.getProduct().getId(),
						orderItem.getProduct().getName(),
						orderItem.getProduct() != null ?
								List.of(new MenuOptionDetailResponseDto(
										orderItem.getProduct().getId(),
										orderItem.getProduct().getName()
								)) : List.of(),
						orderItem.getQuantity()
				))
				.toList();

		return new OrderDetailResponseDto(
				order.getId(),
				order.getUser().getId(),
				order.getProduct().getId(),
				orderItemDtos,
				order.getTotalPrice(),
				order.getStatus(),
				order.getCreatedAt()
		);
	}

	/**
	 * 주문 상세 조회 접근 권한 확인
	 */
	private Order validateDetailOrder(User user, Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new BizException(OrderErrorCode.ORDER_NOT_FOUND));

		if (order.getUser().getId().equals(user.getId()) ||
				order.getProduct().getUser().getId().equals(user.getId())) {
			return order;
		}

		throw new BizException(OrderErrorCode.INACCESSIBLE_ORDER);
	}
}
