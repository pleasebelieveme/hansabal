package org.example.hansabal.domain.wallet.dto.request;

import lombok.Builder;

@Builder
public record ChargeRequestDto(Long id, Long cash) {
}
