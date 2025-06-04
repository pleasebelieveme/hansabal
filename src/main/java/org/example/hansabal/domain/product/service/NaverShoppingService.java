package org.example.hansabal.domain.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.hansabal.domain.product.dto.response.ProductNaverDto;
import org.example.hansabal.domain.product.repository.NaverShoppingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NaverShoppingService {

    private final NaverShoppingRepository repository;

    public NaverShoppingService(NaverShoppingRepository repository) {
        this.repository = repository;
    }

    public List<ProductNaverDto> searchProduct(String query) {
        JsonNode items = repository.search(query);

        List<ProductNaverDto> results = new ArrayList<>();
        for (JsonNode item : items) {
            String title = item.path("title").asText().replaceAll("<.*?>", "");
            String lprice = item.path("lprice").asText();
            results.add(new ProductNaverDto(title, lprice));
        }

        return results;
    }
}
