package org.example.hansabal.domain.auth.service.unit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.domain.auth.service.AuthService;
import org.example.hansabal.domain.users.repository.RedisRepository;
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
}
