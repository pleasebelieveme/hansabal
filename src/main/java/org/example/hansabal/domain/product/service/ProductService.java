package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.dto.request.ProductRequestDto;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.product.dto.response.ProductResponseDto;

import java.util.List;
import java.util.NoSuchElementException;

import static org.example.hansabal.domain.product.exception.ProductErrorCode.INVALID_PRODUCTSTATUS;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto request) {
        Product product = Product.of(request.name(), 10);
        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(INVALID_PRODUCTSTATUS));
        return ProductResponseDto.from(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(INVALID_PRODUCTSTATUS));
        product.updateName(request.name());
        return ProductResponseDto.from(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
