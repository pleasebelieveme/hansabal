package org.example.hansabal.pointHistory.service;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.pointHistory.entity.PointUseHistory;
import org.example.hansabal.domain.pointHistory.repository.PointUseHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointUseHistoryService {

	private final PointUseHistoryRepository pointUseHistoryRepository;

	public void savePointUseHistory(List<PointUseHistory> pointUseHistoryList) {
		pointUseHistoryRepository.saveAll(pointUseHistoryList);
	}

}
