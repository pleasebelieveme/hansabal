package org.example.hansabal.domain.batch.service;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.admin.entity.ProductTradeStatId;
import org.example.hansabal.domain.batch.repository.ProductTradeStatDailyRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductTradeStatBatchService {

	private static final int INSERT_BATCH_SIZE = 100;

	private final ProductTradeStatDailyRepository dailyRepository;
	private final EntityManager em;

	/**
	 * 일별 주문 통계 리스트를 받아 저장하거나 업데이트하는 메서드
	 * - 이미 존재하는 데이터는 값이 다르면 업데이트
	 * - 없는 데이터는 신규 저장
	 * - 실패한 삽입 및 업데이트 작업은 재시도 로직 수행
	 *
	 * @param stats 저장할 ProductStatDaily 리스트
	 */
	@Transactional
	public void saveDaily(List<ProductTradeStatDaily> stats) {
		if (stats.isEmpty()) {
			return;  // 처리할 데이터가 없으면 종료
		}

		// 입력 데이터의 ID 리스트 추출
		List<ProductTradeStatId> ids = stats.stream()
				.map(ProductTradeStatDaily::getId)
				.toList();

		// DB에서 이미 존재하는 데이터 조회 및 Map으로 변환 (ID -> Entity)
		Map<ProductTradeStatId, ProductTradeStatDaily> existingMap = dailyRepository.findAllById(ids).stream()
				.collect(Collectors.toMap(ProductTradeStatDaily::getId, e -> e));

		List<ProductTradeStatDaily> toInsert = new ArrayList<>();
		List<ProductTradeStatDaily> toUpdate = new ArrayList<>();

		// 각 데이터에 대해 신규 저장 또는 업데이트할 목록 분리
		for (ProductTradeStatDaily stat : stats) {
			if (existingMap.containsKey(stat.getId())) {
				ProductTradeStatDaily existing = existingMap.get(stat.getId());
				// 기존 데이터와 비교하여 변경되었으면 업데이트 대상에 추가
				if (!Objects.equals(existing, stat)) {
					existing.update(stat.getTradeCount(), stat.getTotalSales());
					toUpdate.add(existing);
				}
			} else {
				toInsert.add(stat);
			}
		}

		// 삽입 실패 데이터 담을 리스트
		List<ProductTradeStatDaily> failedInsertStats = new ArrayList<>();
		if (!toInsert.isEmpty()) {
			try {
				dailyRepository.saveAll(toInsert);
			} catch (Exception e) {
				// 삽입 실패 시 재시도할 리스트에 추가
				failedInsertStats.addAll(toInsert);
			}
		}

		// 업데이트 실패 데이터 담을 리스트
		List<ProductTradeStatDaily> failedUpdateStats = new ArrayList<>();
		for (ProductTradeStatDaily stat : toUpdate) {
			try {
				// bulkUpdate로 대량 업데이트 시도
				dailyRepository.bulkUpdate(stat.getId(), stat.getTradeCount(), stat.getTotalSales());
			} catch (Exception e) {
				failedUpdateStats.add(stat);
			}
		}

		// JPA 영속성 컨텍스트 초기화
		dailyRepository.flush();
		em.clear();

		// 삽입 실패 데이터 재시도
		if (!failedInsertStats.isEmpty()) {
			retryFailedInsertsBatch(failedInsertStats);
		}
		// 업데이트 실패 데이터 재시도
		if (!failedUpdateStats.isEmpty()) {
			retryDailyFailedUpdates(failedUpdateStats);
		}

		// 모든 실패 데이터 최종 저장 처리
		saveAllFailedStats();
	}

	/**
	 * 삽입 실패한 데이터들을 배치 크기(INSERT_BATCH_SIZE) 단위로 나누어 재시도 처리
	 */
	private void retryFailedInsertsBatch(List<ProductTradeStatDaily> failedStats) {
		List<ProductTradeStatDaily> batch = new ArrayList<>(INSERT_BATCH_SIZE);

		for (ProductTradeStatDaily stat : failedStats) {
			batch.add(stat);
			if (batch.size() == INSERT_BATCH_SIZE) {
				saveBatch(batch);
				batch.clear();
			}
		}

		if (!batch.isEmpty()) {
			saveBatch(batch);
		}
	}

	/**
	 * 배치 단위로 삽입 재시도 수행
	 */
	private void saveBatch(List<ProductTradeStatDaily> batch) {
		try {
			dailyRepository.saveAll(batch);
			dailyRepository.flush();
			em.clear();
		} catch (Exception e) {
			// 배치 삽입 실패 시 개별 삽입 재시도
			retryDailyFailedInserts(batch);
		}
	}

	/**
	 * 삽입에 최종 실패한 데이터들을 하나씩 다시 저장 시도,
	 * 실패 시 로그 남기고 별도 실패 테이블에 저장
	 */
	private void retryDailyFailedInserts(List<ProductTradeStatDaily> failedStats) {
		for (ProductTradeStatDaily stat : failedStats) {
			try {
				dailyRepository.save(stat);
				dailyRepository.flush();
			} catch (Exception e) {
				log.error("Final insert failure for stat: {}", stat.getId(), e);
				saveToFailedTable(stat, e);  // 실패 데이터 별도 테이블 저장
			} finally {
				em.clear();
			}
		}
	}

	/**
	 * 업데이트 실패한 데이터에 대해 다시 업데이트 시도,
	 * 최종 실패 시 로그 출력 및 별도 실패 테이블에 저장
	 */
	private void retryDailyFailedUpdates(List<ProductTradeStatDaily> failedStats) {
		for (ProductTradeStatDaily stat : failedStats) {
			try {
				dailyRepository.bulkUpdate(stat.getId(), stat.getTradeCount(), stat.getTotalSales());
			} catch (Exception e) {
				log.error("Final failure after retry: stat = {}", stat.getId(), e);
				saveToFailedTable(stat, e);  // 실패 데이터 별도 테이블 저장
			}
		}

		dailyRepository.flush();
		em.clear();
	}

	/**
	 * 실패한 데이터를 별도 테이블에 저장하는 메서드 (구현 필요)
	 */
	public void saveToFailedTable(ProductTradeStatDaily stat, Exception e) {
		// TODO: 실패 데이터 저장 로직 구현
	}

	/**
	 * 모든 실패 데이터에 대한 최종 처리 메서드 (구현 필요)
	 */
	public void saveAllFailedStats() {
		// TODO: 최종 실패 데이터 처리 로직 구현
	}
}
