package org.example.hansabal.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TradeRequestDto(@NotNull String title, String contents, @NotNull Long traderId
){
}
