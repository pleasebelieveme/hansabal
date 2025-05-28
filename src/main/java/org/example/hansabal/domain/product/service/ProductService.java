package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.product.dto.request.ProductRequestDto;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.product.dto.response.ProductResponseDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = Product.of(requestDto.getName(), 10);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct.getProductId(), savedProduct.getName());
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다."));
        return new ProductResponseDto(product.getProductId(), product.getName());
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponseDto(product.getProductId(), product.getName()))
                .toList();
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 상품이 존재하지 않습니다."));
        product.updateName(requestDto.getName());
        return new ProductResponseDto(product.getProductId(), product.getName());
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
