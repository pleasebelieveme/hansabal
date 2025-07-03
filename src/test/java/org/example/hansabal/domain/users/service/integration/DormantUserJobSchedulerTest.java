package org.example.hansabal.domain.users.service.integration;

import org.example.hansabal.HansabalApplication;
import org.example.hansabal.config.TestBatchConfig;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = HansabalApplication.class)
@ActiveProfiles("test") // H2는 FULLTEXT지원 X
@Import({DormantUserJobConfig.class, TestBatchConfig.class})
@Testcontainers
@Sql(scripts = "classpath:org/springframework/batch/core/schema-mysql.sql")
public class DormantUserJobSchedulerTest {

    @TestConfiguration
    public static class BatchTestConfig {
        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job markDormantUserJob;

    @Autowired
    private UserRepository userRepository;

    private User dormantTarget;
    private User activeUser;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
            .withExposedPorts(6379);

    static {
        mysql.start();
        redis.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }


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
