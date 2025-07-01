package org.example.hansabal.domain.trade.service;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.dto.response.CartItemResponse;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.entity.TradeItem;
import org.example.hansabal.domain.trade.repository.TradeItemRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;

@Service
@RequiredArgsConstructor
public class TradeItemService {

	private final TradeItemRepository tradeItemRepository;
	private final TradeRepository tradeRepository;
	private final ProductRepository productRepository;

	public List<Long> addtradeItem(Long tradeId, List<CartItemResponse> items) {

		Trade trade = tradeRepository.findById(tradeId)
				.orElseThrow(() -> new BizException(TradeErrorCode.Trade_NOT_FOUND));

		List<Long> addedIds = new ArrayList<>();

		for (CartItemResponse item : items) {

			Product product = productRepository.findById(item.ProductId())
					.orElseThrow(() -> new BizException(ProductErrorCode.PRODUCT_NOT_FOUND));

			TradeItem tradeItem = new TradeItem(item.quantity(), trade, product);

			TradeItem savedTradeItem = tradeItemRepository.save(tradeItem);
			addedIds.add(savedTradeItem.getId());
		}
		return addedIds;
	}

	public List<Long> getTradeItemIds(Long tradeId) {

		return tradeItemRepository.findIdsByTradeId(tradeId);
	}
}
