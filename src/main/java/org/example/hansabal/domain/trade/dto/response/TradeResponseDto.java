package org.example.hansabal.domain.trade.dto.response;

import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;

@Builder
public record TradeResponseDto(Long id, String title, String contents, User trader){
	}
