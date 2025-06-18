package org.example.hansabal.domain.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.product.repository.CartRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CartScheduler {

	private final CartRepository cartRepository;

	/**
	 * 매일 자정(00:00)에 실행되는 스케줄러 메서드
	 * 하루가 지난(1일 이상 지난) 장바구니 데이터를 삭제한다.
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void clearExpiredCarts() {
		// 현재 시간에서 1일(24시간) 전 시점을 계산
		LocalDateTime expiredTime = LocalDateTime.now().minusDays(1);

		// 만료된(하루 지난) 장바구니 데이터를 DB에서 삭제
		cartRepository.deleteAllExpired(expiredTime);

		// 작업 완료 로그 출력
		System.out.println("하루 지난 장바구니 삭제 완료");
	}
}
