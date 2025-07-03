package org.example.hansabal.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestBatchConfig {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job markDormantUserJob;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        JobLauncherTestUtils utils = new JobLauncherTestUtils();
        utils.setJobLauncher(jobLauncher);   // 반드시 주입
        utils.setJob(markDormantUserJob);    // Job도 주입
        return utils;
    }
}