package org.example.hansabal.domain.users.service.integration;

import org.example.hansabal.domain.users.batch.config.DormantUserJobConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Import(DormantUserJobConfig.class) // JobConfig 수동 등록 (필요 시)
public class DormantUserJobSchedulerTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job markDormantUserJob;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(markDormantUserJob);
    }

    @Test
    void testRunDormantUserJob() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        System.out.println("StepExecutions: " + execution.getStepExecutions());
    }
}
