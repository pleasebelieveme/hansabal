package org.example.hansabal.domain.trade.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestsListResponseDto {
	private Long count;
	private List<RequestsResponseDto> requestsList;
}
