package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;

@Builder
public record RequestsResponseDto (Long requestesId, RequestStatus status, Trade tradeId, User requesterId){
}
