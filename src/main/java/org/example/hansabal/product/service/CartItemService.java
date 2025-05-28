package org.example.hansabal.product.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.product.dto.request.ChangeQuantityRequest;
import org.example.hansabal.product.dto.response.ChangeQuantityResponse;
import org.example.hansabal.product.dto.response.ItemResponse;
import org.example.hansabal.product.entity.Cart;
import org.example.hansabal.product.entity.CartItem;
import org.example.hansabal.product.entity.Product;
import org.example.hansabal.product.entity.ProductStatus;
import org.example.hansabal.product.exception.ProductErrorCode;
import org.example.hansabal.product.repository.CartItemRepository;
import org.example.hansabal.product.repository.CartRepository;
import org.example.hansabal.product.repository.ProductRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ProductServiceUtil serviceUtil;
    private final RedissonClient redissonClient;

    private static final String PRODUCT_LOCK_PREFIX = "LOCK:STOCK:";

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
    public void productPayment(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cartId);

        if(cartItems.isEmpty()){
            throw new BizException(ProductErrorCode.NO_CONTENTS);
        }

        for(CartItem cartItem : cartItems){
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            String lockKey = PRODUCT_LOCK_PREFIX + product.getId(); // 락 키를  "LOCK:STOCK:{id}" 형태로 생성
            RLock lock = redissonClient.getLock(lockKey); // 분산 락 객체

            boolean isLocked = false;

            try {
                isLocked = lock.tryLock(3,5, TimeUnit.SECONDS); // 최대 3초 기다리고 획득 후 5초뒤 자동 만료
                if(!isLocked){
                    throw new BizException(ProductErrorCode.LOCK_FAILED);
                }

                serviceUtil.decreaseProduct(product,quantity);
            } catch (InterruptedException e) {
                /* Thread.interrupt() 메서드는 스레드를 중간에 종료시킬 수 있는 메서드
                 *  다만 모든 상황에서 스레드가 종료되는건 아님
                 *  그 이유는 쓰레드가 일시 정지 상태일때만 정지 시킴
                 *  즉, 원하는 조건 및 시점에서 스레드를 종료시키기 위한 함수임
                 * */
                Thread.currentThread().interrupt();
                throw new BizException(ProductErrorCode.LOCK_INTERRUPTED);
            } finally {
                if(isLocked && lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }

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
