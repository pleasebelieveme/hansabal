package org.example.hansabal.domain.auth.service.integration;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
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

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
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
        String token = tokenService.createTokens(user.getId(), user.getUserRole()).getAccessToken();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        // when
        authService.logout(request);

        // then: 블랙리스트 등록, 리프레시 삭제가 호출됐는지 검증
        verify(redisRepository, times(1)).saveBlackListToken(anyString(), anyLong());
        verify(redisRepository, times(1)).deleteRefreshToken(user.getId());
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
    void 리프레시토큰의_정보가_유효하지_않으면_예외발생() {
        // given
        when(jwtUtil.validateToken("validRefreshToken")).thenReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() -> authService.reissue("Bearer validRefreshToken"))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰 정보가 일치하지 않습니다.");
    }
}
