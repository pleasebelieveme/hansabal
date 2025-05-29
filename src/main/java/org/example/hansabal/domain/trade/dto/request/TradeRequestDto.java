package org.example.hansabal.domain.trade.dto.request;

import lombok.Builder;

@Builder
public record TradeRequestDto(String title,String contents,Long traderId
){
}
