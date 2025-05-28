package org.example.anonymous.domain.product.service;

import org.example.anonymous.domain.product.entity.Product;
import org.example.anonymous.domain.product.exception.ProductErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceUtil {
    public void decreaseProduct(Product product, int quantity){
        if(product.getQuantity()<quantity){
            throw new BizException(ProductErrorCode.INVALID_PRODUCTSTATUS);
        }

        int result = product.getQuantity() - quantity;

        product.updateQuantity(result);
    }
}
