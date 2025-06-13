package org.example.hansabal.domain.auth.service.unit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.service.AuthService;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AuthUnitTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 리프레시토큰_정보가_유효하지_않으면_예외발생() {
        // given
        String refreshToken = "validRefreshToken";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue("Bearer " + refreshToken))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("리프레시 토큰 정보가 일치하지 않습니다.");
    }

    @Test
    void 리프레시토큰이_레디스에_저장된_것과_일치하지_않으면_REUSED_REFRESH_TOKEN_예외발생() {
        // given
        String refreshToken = "validRefreshToken";
        UserAuth mockUserAuth = new UserAuth(1L, UserRole.USER);

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUserAuth(refreshToken)).thenReturn(mockUserAuth);
        when(redisRepository.validateRefreshToken(mockUserAuth.getId(), refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue("Bearer " + refreshToken))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("이미 사용된 리프레쉬 토큰입니다");
    }
}
