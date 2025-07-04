package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.dto.request.ProductRequestDto;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.product.dto.response.ProductResponseDto;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request  ) {

        Product product = Product.of(request.name(), request.price(), request.quantity());
        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(ProductErrorCode.INVALID_PRODUCTSTATUS));
        return ProductResponseDto.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts( int page, int size) {
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, size);

        return productRepository.getAllProducts(pageable);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BizException(ProductErrorCode.INVALID_PRODUCTSTATUS));
        product.updateName(request.name());
        return ProductResponseDto.from(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
