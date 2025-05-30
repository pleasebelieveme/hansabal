package org.example.hansabal.domain.trade.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestsListResponseDto {
	private Long count;
	private List<RequestsResponseDto> requestsList;
}
