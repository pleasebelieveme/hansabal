package org.example.hansabal.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.product.dto.response.ShopItem;
import org.example.hansabal.domain.product.service.ShopSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopSearchController {

    private final ShopSearchService shopSearchService;

    @GetMapping("/search")
    public List<ShopItem> searchShop(@RequestParam String query) {
        return shopSearchService.searchShop(query);
    }
}