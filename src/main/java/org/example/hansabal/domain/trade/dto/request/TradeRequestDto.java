package org.example.hansabal.domain.trade.dto.request;

import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;

@Builder
public record TradeRequestDto(String title,String contents,User trader
){
}
