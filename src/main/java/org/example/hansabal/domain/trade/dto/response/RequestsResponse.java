package org.example.hansabal.domain.trade.dto.response;


import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Requests;

import lombok.Builder;

@Builder
public record RequestsResponse(Long requestsId, RequestStatus status, Long tradeId, Long requesterId){
	public static RequestsResponse from(Requests requests){
		return new RequestsResponse(requests.getId(), requests.getStatus(),
			requests.getTrade().getId(), requests.getRequester().getId());
	}
}
