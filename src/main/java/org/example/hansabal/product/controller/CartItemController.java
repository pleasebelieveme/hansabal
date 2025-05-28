package org.example.hansabal.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.product.dto.request.ChangeQuantityRequest;
import org.example.hansabal.product.dto.response.ChangeQuantityResponse;
import org.example.hansabal.product.dto.response.ItemResponse;
import org.example.hansabal.product.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/{cartId}")
    public ResponseEntity<ItemResponse> createItems(
            @PathVariable Long cartId,
            @RequestParam Long foodId,
            @RequestParam(defaultValue = "1") int quantity){

        ItemResponse items = cartItemService.createItems(foodId, cartId, quantity);

        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<ItemResponse>> findAllItems(@PathVariable Long cartId){

        List<ItemResponse> allItems = cartItemService.findAllItems(cartId);

        return ResponseEntity.status(HttpStatus.OK).body(allItems);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteItems(@PathVariable Long cartItemId){

        cartItemService.deleteItems(cartItemId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<Void> productPayment(@PathVariable Long cartId){

        cartItemService.productPayment(cartId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<ChangeQuantityResponse> changeQuantity(
            @RequestBody ChangeQuantityRequest request,
            @PathVariable Long cartItemId) {

        ChangeQuantityResponse changedQuantity = cartItemService.changeQuantity(request, cartItemId);

        return ResponseEntity.status(HttpStatus.OK).body(changedQuantity);
    }
}