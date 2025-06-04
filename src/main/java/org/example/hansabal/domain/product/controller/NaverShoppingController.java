package org.example.hansabal.domain.product.controller;

import org.example.hansabal.domain.product.dto.response.ProductNaverDto;
import org.example.hansabal.domain.product.service.NaverShoppingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NaverShoppingController {

    private final NaverShoppingService shoppingService;

    public NaverShoppingController(NaverShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @GetMapping("/search")
    public List<ProductNaverDto> search(@RequestParam String query) {
        return shoppingService.searchProduct(query);
    }
}
