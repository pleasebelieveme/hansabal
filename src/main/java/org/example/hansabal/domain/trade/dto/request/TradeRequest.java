package org.example.hansabal.domain.trade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TradeRequest(@NotBlank String title, String contents, @NotNull Long price){
}
