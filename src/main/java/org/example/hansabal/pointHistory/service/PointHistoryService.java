package org.example.hansabal.pointHistory.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.pointHistory.entity.PointHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUseHistory;
import org.example.hansabal.domain.pointHistory.entity.PointUsed;
import org.example.hansabal.domain.pointHistory.repository.PointHistoryRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

	private final PointHistoryRepository pointHistoryRepository;
	private final UserRepository userRepository;
	private final PointUseHistoryService pointUseHistoryService;

	public Integer getRemainingPoint(Long userId) {

		return pointHistoryRepository.sumRemainByUserIdAndPointUsed(
			userId, PointUsed.UNUSED);
	}

	@Transactional
	public void usePoint(Long userId, Integer point) {
		List<PointUseHistory> pointUseHistoryList = pointHistoryRepository.applyUserPointUsage(userId, point);

		pointUseHistoryService.savePointUseHistory(pointUseHistoryList);
	}

	public void getPoint(Long userId, Integer totalPrice) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BizException(UserErrorCode.INVALID_REQUEST));

		PointHistory pointHistory = new PointHistory(totalPrice / 100, totalPrice / 100, user);

		pointHistoryRepository.save(pointHistory);

	}
}
