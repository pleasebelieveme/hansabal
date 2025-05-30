package org.example.hansabal.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RequestsRequestDto(@NotNull Long tradeId) {
}
