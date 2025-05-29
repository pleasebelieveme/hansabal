package org.example.hansabal.domain.product.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
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
