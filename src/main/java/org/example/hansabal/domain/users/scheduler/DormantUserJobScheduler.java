package org.example.hansabal.domain.users.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DormantUserJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job markDormantUserJob;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void runDormantUserJob() {
        try {
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 매 실행마다 유니크해야 함
                    .toJobParameters();

            jobLauncher.run(markDormantUserJob, parameters);
            log.info("휴면 계정 배치 Job 실행 완료");
        } catch (Exception e) {
            log.error("휴면 계정 Job 실행 실패", e);
        }
    }
}