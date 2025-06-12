package org.example.hansabal.domain.auth.service.integration;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;


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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.*;

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
        // given: 유저 조회 및 토큰 생성
        User user = userRepository.findByEmailOrElseThrow("test@email.com");
        TokenResponse tokens = tokenService.generateTokens(user.getId(), user.getUserRole());
        tokenService.saveRefreshToken(user.getId(), tokens.getRefreshToken());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokens.getAccessToken());

        // when: 로그아웃 수행
        authService.logout(request);

        // then: AccessToken 유효성 확인 (여전히 유효하지만 블랙리스트에 저장됐는지 확인)
        assertThat(jwtUtil.validateToken(tokens.getAccessToken())).isTrue();

        // Redis에 AccessToken이 블랙리스트로 저장되어 있는지 확인
        boolean isBlackListed = redisRepository.validateKey(tokens.getAccessToken());
        assertThat(isBlackListed).isTrue();

        // 리프레시 토큰이 삭제되어 더 이상 유효하지 않아야 함
        boolean isRefreshTokenValid = redisRepository.validateRefreshToken(user.getId(), tokens.getRefreshToken());
        assertThat(isRefreshTokenValid).isFalse();
    }

    @Test
    void 로그아웃_중_token이_null이면_예외_없이_조용히_무시된다() {
        // given: Authorization 헤더가 없음 → extractToken()이 null 반환
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when & then
        assertThatCode(() -> authService.logout(request))
                .doesNotThrowAnyException();
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
        TokenResponse tokenResponse = tokenService.generateTokens(user.getId(), user.getUserRole());
        tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());
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
