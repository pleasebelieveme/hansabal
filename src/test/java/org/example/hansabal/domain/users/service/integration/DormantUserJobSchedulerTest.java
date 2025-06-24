package org.example.hansabal.domain.users.service.integration;

import org.example.hansabal.domain.users.batch.config.DormantUserJobConfig;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test-batch") // H2 기반 설정 적용
@Import(DormantUserJobConfig.class)
public class DormantUserJobSchedulerTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job markDormantUserJob;

    @Autowired
    private UserRepository userRepository;

    private User dormantTarget;
    private User activeUser;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(markDormantUserJob);
        userRepository.deleteAll();

        // 휴면 대상 유저: 2년 전 로그인
        dormantTarget = userRepository.save(
                User.builder()
                        .email("old@example.com")
                        .password("encoded")
                        .name("Old User")
                        .nickname("oldie")
                        .userRole(UserRole.USER)
                        .userStatus(UserStatus.ACTIVE)
                        .lastLoginAt(LocalDateTime.now().minusYears(2))
                        .build()
        );
        System.out.println("✅라스트 로그인 시간 2년전 : " + dormantTarget.getLastLoginAt());

        // 최근 로그인한 유저: 10일 전
        activeUser = userRepository.save(
                User.builder()
                        .email("recent@example.com")
                        .password("encoded")
                        .name("Recent User")
                        .nickname("rec")
                        .userRole(UserRole.USER)
                        .userStatus(UserStatus.ACTIVE)
                        .lastLoginAt(LocalDateTime.now().minusDays(10))
                        .build()
        );
        System.out.println("✅라스트 로그인 시간 10일전 : " + activeUser.getLastLoginAt());
    }

    @Test
    void testRunDormantUserJob() throws Exception {
        // given
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        // then
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // 휴면 유저만 상태 변경되었는지 확인
        User updatedDormant = userRepository.findById(dormantTarget.getId()).orElseThrow();
        User updatedActive = userRepository.findById(activeUser.getId()).orElseThrow();

        assertThat(updatedDormant.getUserStatus()).isEqualTo(UserStatus.DORMANT);
        assertThat(updatedActive.getUserStatus()).isEqualTo(UserStatus.ACTIVE);

        System.out.println("✅ StepExecutions: " + execution.getStepExecutions());
    }
}
