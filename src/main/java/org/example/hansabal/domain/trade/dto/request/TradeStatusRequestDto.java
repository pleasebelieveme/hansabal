package org.example.hansabal.domain.trade.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.TradeStatus;

public record TradeStatusRequestDto(
	@NotNull(message = "주문 상태는 필수 입력 값입니다.")
	TradeStatus tradeStatus
) {
    public RequestStatus requestStatus(){
            return RequestStatus.valueOf(tradeStatus.name());

    }
}
