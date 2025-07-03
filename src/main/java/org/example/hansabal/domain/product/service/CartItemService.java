package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.dto.request.CartCreateRequest;
import org.example.hansabal.domain.product.dto.request.ChangeQuantityRequest;
import org.example.hansabal.domain.product.dto.response.CartCreateResponse;
import org.example.hansabal.domain.product.dto.response.ChangeQuantityResponse;
import org.example.hansabal.domain.product.dto.response.ItemResponse;
import org.example.hansabal.domain.product.entity.Cart;
import org.example.hansabal.domain.product.entity.CartItem;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.entity.ProductStatus;
import org.example.hansabal.domain.product.exception.CartErrorCode;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
import org.example.hansabal.domain.product.repository.CartItemRepository;
import org.example.hansabal.domain.product.repository.CartRepository;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.wallet.service.WalletService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ProductServiceUtil serviceUtil;
    private final RedissonClient redissonClient;

    private static final String PRODUCT_LOCK_PREFIX = "LOCK:STOCK:";


    @Transactional

    public CartCreateResponse createCart(CartCreateRequest request, UserAuth userAuth) {

        User user = userRepository.findById(userAuth.getId())

                .orElseThrow(() -> new BizException(UserErrorCode.INVALID_REQUEST));

        boolean exists = cartRepository.findByUserId(user.getId()).stream().findAny().isPresent();

        if (exists) {

            throw new BizException(CartErrorCode.CART_ALREADY_EXISTS);

        }

        if (request.quantity() <= 0) {

            throw new BizException(CartErrorCode.INVALID_QUANTITY);

        }

        Cart cart = new Cart(user);

        Cart save = cartRepository.save(cart);

        return CartCreateResponse.from(save);
    }


    @Transactional
    public ItemResponse createItems(Long productId, Long cartId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new BizException(ProductErrorCode.INVALID_ID));

        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BizException(ProductErrorCode.INVALID_ID));

        if(product.getProductStatus().equals(ProductStatus.SOLD_OUT)){
            throw new BizException(ProductErrorCode.INVALID_PRODUCTSTATUS);
        }

        cartItemRepository.findByCartAndProduct(cart, product).ifPresent(
                item -> {
                    throw new BizException(ProductErrorCode.DUPLICATED_LIST);
                });


        CartItem cartItem = new CartItem(quantity,cart,product);

        CartItem save = cartItemRepository.save(cartItem);

        return ItemResponse.from(save);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findAllItems(Long cartId) {

        List<CartItem> allByCart = cartItemRepository.findAllByCart(cartId);

        return allByCart.stream().map(ItemResponse::from).toList();
    }

    @Transactional
    public void deleteItems(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new BizException(ProductErrorCode.INVALID_ID));

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void productPayment(Long cartId, UserAuth userAuth) {
        User user = userRepository.findById(userAuth.getId())
                .orElseThrow(() -> new BizException(UserErrorCode.INVALID_REQUEST));

        List<CartItem> cartItems = cartItemRepository.findAllByCart(cartId);

        if(cartItems.isEmpty()){
            throw new BizException(ProductErrorCode.NO_CONTENTS);
        }

        long totalPrice = 0L; // 총 결제 금액
        for(CartItem cartItem : cartItems){
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // 총 가격 계산
            totalPrice += (long) product.getPrice() * quantity;

            String lockKey = PRODUCT_LOCK_PREFIX + product.getId();
            RLock lock = redissonClient.getLock(lockKey);

            boolean isLocked = false;
            try {
                isLocked = lock.tryLock(3,5, TimeUnit.SECONDS);
                if(!isLocked){
                    throw new BizException(ProductErrorCode.LOCK_FAILED);
                }

                serviceUtil.decreaseProduct(product,quantity); // 상품 재고 감소
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BizException(ProductErrorCode.LOCK_INTERRUPTED);
            } finally {
                if(isLocked && lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
        walletService.walletCartPay(user, cartId, totalPrice);

        // 지갑 결제까지 성공하면 장바구니 아이템 삭제
        cartItemRepository.deleteAllByCartId(cartId);
    }

    @Transactional
    public ChangeQuantityResponse changeQuantity(ChangeQuantityRequest request, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new BizException(ProductErrorCode.INVALID_ID));

        int updatedQuantity = cartItem.getQuantity()+request.quantity();

        if(cartItem.getQuantity()<=0){
            deleteItems(cartItemId);
            return new ChangeQuantityResponse(0);
        }

        cartItem.updateQuantity(updatedQuantity);

        return ChangeQuantityResponse.from(cartItem);
    }
}
