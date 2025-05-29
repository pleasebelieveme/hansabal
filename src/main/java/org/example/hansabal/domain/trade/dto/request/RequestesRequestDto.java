package org.example.hansabal.domain.trade.dto.request;

import org.example.hansabal.domain.trade.entity.Trade;

import lombok.Builder;

@Builder
public record RequestesRequestDto(Trade tradeId) {
}
