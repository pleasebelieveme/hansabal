package org.example.hansabal.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.product.dto.request.CartCreateRequest;
import org.example.hansabal.domain.product.dto.request.ChangeQuantityRequest;
import org.example.hansabal.domain.product.dto.response.CartCreateResponse;
import org.example.hansabal.domain.product.dto.response.ChangeQuantityResponse;
import org.example.hansabal.domain.product.dto.response.ItemResponse;
import org.example.hansabal.domain.product.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/cart")
    public ResponseEntity<CartCreateResponse> createCart(
            @RequestBody CartCreateRequest request,
            @AuthenticationPrincipal UserAuth userAuth) {

        CartCreateResponse response = cartItemService.createCart(request, userAuth);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cartId}")
    public ResponseEntity<ItemResponse> createItems(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity){

        ItemResponse items = cartItemService.createItems(productId, cartId, quantity);

        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<ItemResponse>> findAllItems(@PathVariable Long cartId){

        List<ItemResponse> allItems = cartItemService.findAllItems(cartId);

        return ResponseEntity.status(HttpStatus.OK).body(allItems);
    }

    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<Void> deleteItems(@PathVariable Long cartItemId){

        cartItemService.deleteItems(cartItemId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<Void> productPayment(@PathVariable Long cartId,@AuthenticationPrincipal UserAuth userAuth){

        cartItemService.productPayment(cartId, userAuth);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/cart/{cartItemId}")
    public ResponseEntity<ChangeQuantityResponse> changeQuantity(
            @RequestBody ChangeQuantityRequest request,
            @PathVariable Long cartItemId) {

        ChangeQuantityResponse changedQuantity = cartItemService.changeQuantity(request, cartItemId);

        return ResponseEntity.status(HttpStatus.OK).body(changedQuantity);
    }
}