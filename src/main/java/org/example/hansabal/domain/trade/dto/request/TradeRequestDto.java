package org.example.hansabal.domain.trade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TradeRequestDto(@NotBlank String title, String contents, @NotNull Long traderId, @NotNull Long price
){
}
