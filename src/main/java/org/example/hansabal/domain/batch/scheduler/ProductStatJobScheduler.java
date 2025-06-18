package org.example.hansabal.domain.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductStatJobScheduler {

	private final JobLauncher jobLauncher;
	private final Job ProductStatDailyJob;

	/**
	 * 매일 00:01:00시에 실행되는 스케줄러 메서드
	 * ProductStatDailyJob 배치 잡을 실행한다.
	 *
	 * JobParameters에 현재 시간을 추가하여 매번 다른 파라미터로 잡이 실행되도록 한다.
	 */
	@Scheduled(cron = "0 1 0 * * *")
	public void run() {
		try {
			log.info("ProductStatDailyJob started");
			jobLauncher.run(ProductStatDailyJob,
					new JobParametersBuilder()
							.addLong("time", System.currentTimeMillis())  // 현재 시간을 파라미터로 추가
							.toJobParameters());
		} catch (Exception e) {
			log.error("ProductStatDailyJob failed", e);  // 실행 실패 시 에러 로그 출력
		}
	}
}
