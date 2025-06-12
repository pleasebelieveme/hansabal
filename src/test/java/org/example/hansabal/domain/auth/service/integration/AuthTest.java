package org.example.hansabal.domain.auth.service.integration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.AuthService;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"/user_test_db.sql"}
        ,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
public class AuthTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        // MySQL 설정
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);

        // Redis 설정
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeAll
    static void checkRedisConnection() {
        System.out.println(">>> Redis running? " + redis.isRunning());
        System.out.println(">>> Redis host: " + redis.getHost());
        System.out.println(">>> Redis mapped port: " + redis.getMappedPort(6379));

        // Lettuce 등 Redis 클라이언트로 ping 테스트
        RedisClient client = RedisClient.create("redis://" + redis.getHost() + ":" + redis.getMappedPort(6379));
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            String pong = connection.sync().ping();
            System.out.println("Redis PING response: " + pong);
        }
    }
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisRepository redisRepository;


    @Test
    void 로그인_성공() {
        // given
        LoginRequest request = new LoginRequest("test@email.com", "!Aa123456");

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }

    @Test
    void 로그인_실패_비밀번호_불일치() {
        // given
        LoginRequest request = new LoginRequest("test@email.com", "WrongPassword123!");

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("유효하지 않은 비밀번호입니다.");
    }

    @Test
    void 로그인_실패_존재하지_않는_이메일() {
        // given
        LoginRequest request = new LoginRequest("notexist@email.com", "!Aa123456");

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

    @Test
    void 로그아웃_성공() {
        // given
        User user = userRepository.findByEmailOrElseThrow("test@email.com");
        TokenResponse tokens = tokenService.createTokens(user.getId(), user.getUserRole());
        String accessToken = tokens.getAccessToken();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        // when
        authService.logout(request);

        // 블랙리스트 토큰 존재 여부 체크
        boolean isBlackListed = redisRepository.validateKey(accessToken);
        assertThat(isBlackListed).isTrue();

        // 리프레시 토큰 존재 여부 체크 (일치 여부 판단)
        boolean refreshTokenValid = redisRepository.validateRefreshToken(user.getId(), "기대하는_리프레시_토큰_값");
        assertThat(refreshTokenValid).isFalse(); // 로그아웃 후면 삭제되어 false여야 함
    }
    @Test
    void 리프레시토큰이_널이거나_Bearer_로_시작하지_않으면_예외발생() {
        // given
        // when & then
        Assertions.assertThatThrownBy(() -> authService.reissue(null))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰 정보가 일치하지 않습니다.");
        Assertions.assertThatThrownBy(() -> authService.reissue("InvalidToken"))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰 정보가 일치하지 않습니다.");
    }

    @Test
    void 레디스에_저장된_리프레시토큰과_일치하지_않으면_예외가_발생한다() {
        // given
        User user = userRepository.findByEmailOrElseThrow("test@email.com");

        // 실제 토큰 생성 (유효한 리프레시 토큰)
        TokenResponse tokenResponse = tokenService.createTokens(user.getId(), user.getUserRole());
        String validRefreshToken = tokenResponse.getRefreshToken();

        // Redis에 실제 저장 (saveRefreshToken 메서드 사용)
        redisRepository.saveRefreshToken(user.getId(), validRefreshToken, 1000 * 60 * 60); // 1시간

        // '잘못된' 리프레시 토큰 생성 (저장된 것과 다른 값)
        String invalidRefreshToken = "someInvalidRefreshToken";

        // when & then
        Assertions.assertThatThrownBy(() -> authService.reissue("Bearer " + invalidRefreshToken))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰 정보가 일치하지 않습니다.");
    }
}
