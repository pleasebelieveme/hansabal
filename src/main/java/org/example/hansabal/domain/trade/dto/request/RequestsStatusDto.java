package org.example.hansabal.domain.trade.dto.request;

import org.example.hansabal.domain.trade.entity.RequestStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RequestsStatusDto(@NotNull RequestStatus requestStatus) {
}
