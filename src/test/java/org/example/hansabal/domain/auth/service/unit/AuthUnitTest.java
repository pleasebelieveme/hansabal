package org.example.hansabal.domain.auth.service.unit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.AuthService;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AuthUnitTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private RedisRepository redisRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Authorization_헤더가_Bearer_형식이_아니면_예외() {
        String invalidHeader = "InvalidToken";

        assertThatThrownBy(() -> authService.reissue(invalidHeader))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("Authorization 헤더 형식이 잘못되었습니다");
    }

    @Test
    void 리프레시토큰이_유효하지_않으면_예외() {
        String refreshToken = "Bearer validToken";
        when(jwtUtil.validateToken("validToken")).thenReturn(false);

        assertThatThrownBy(() -> authService.reissue(refreshToken))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰이 유효하지 않습니다");
    }

    @Test
    void 리프레시토큰이_레디스에_저장된_것과_일치하지_않으면_REUSED_REFRESH_TOKEN_예외발생() {
        // given
        String refreshToken = "validRefreshToken";
        UserAuth mockUserAuth = new UserAuth(1L, UserRole.USER, "testnickname");

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUserAuth(refreshToken)).thenReturn(mockUserAuth);
        when(redisRepository.validateRefreshToken(mockUserAuth.getId(), refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue("Bearer " + refreshToken))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이미 사용된 리프레쉬 토큰입니다");
    }

    @Test
    void 로그인_1년_이상_미접속시_휴면_처리_후_예외_발생() {
        // given
        User dormantUser = spy(User.builder()
                .email("user@email.com")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now().minusYears(2))
                .build());

        LoginRequest loginRequest = new LoginRequest("user@email.com", "inputPassword");

        when(userRepository.findByEmailOrElseThrow(loginRequest.email())).thenReturn(dormantUser);
        when(passwordEncoder.matches(loginRequest.password(), dormantUser.getPassword())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("휴면");

        verify(dormantUser, times(1)).markAsDormant();  // 상태 변경 호출되었는지 확인
        verify(userRepository).save(dormantUser);       // 저장도 되었는지 확인
    }

    @Test
    void 로그인_최근접속자는_정상적으로_로그인된다() {
        // given
        User recentUser = User.builder()
                .id(1L)
                .email("recent@email.com")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .nickname("nick")
                .lastLoginAt(LocalDateTime.now().minusDays(30)) // 최근 로그인
                .build();

        LoginRequest loginRequest = new LoginRequest("recent@email.com", "inputPassword");

        when(userRepository.findByEmailOrElseThrow(loginRequest.email())).thenReturn(recentUser);
        when(passwordEncoder.matches(loginRequest.password(), recentUser.getPassword())).thenReturn(true);
        when(tokenService.generateTokens(recentUser.getId(), recentUser.getUserRole(), recentUser.getNickname()))
                .thenReturn(new TokenResponse("access", "refresh"));

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        verify(userRepository).save(recentUser); // 마지막 로그인 시간 저장
        verify(tokenService).saveRefreshToken(recentUser.getId(), "refresh");

        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
    }
}
