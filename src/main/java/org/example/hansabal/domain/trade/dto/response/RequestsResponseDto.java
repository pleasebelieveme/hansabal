package org.example.hansabal.domain.trade.dto.response;


import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Requests;

import lombok.Builder;

@Builder
public record RequestsResponseDto(Long requestsId, RequestStatus status, Long tradeId, Long requesterId){
	public static RequestsResponseDto from(Requests requests){
		return new RequestsResponseDto(requests.getId(), requests.getStatus(),
			requests.getTrade().getId(), requests.getRequester().getId());
	}
}
