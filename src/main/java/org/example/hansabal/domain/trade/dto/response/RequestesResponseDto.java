package org.example.hansabal.domain.trade.dto.response;


import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Requestes;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;

@Builder
public record RequestesResponseDto (Long requestesId, RequestStatus status, Trade tradeId, User requesterId){
	public static RequestesResponseDto from(Requestes requestes){
		return new RequestesResponseDto(requestes.getRequestesId(), requestes.getStatus(), requestes.getTradeId(), requestes.getRequesterId());
	}
}
