package org.example.hansabal.domain.users.service.unit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.AuthService;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDormantUnitTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Test
    void 로그인_직전_1년_지났다면_휴면처리후_예외_발생() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded")
                .userStatus(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now().minusYears(2)) // 2년 전
                .build();

        given(userRepository.findByEmailOrElseThrow(any())).willReturn(user);

        // when & then
        assertThrows(BizException.class, () -> {
            authService.login(new LoginRequest("test@example.com", "password"));
        });

        assertEquals(UserStatus.DORMANT, user.getUserStatus());
        verify(userRepository).save(user); // 휴면처리 저장 확인
    }

    @Test
    void 상태가_휴면이면_예외_발생() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded")
                .userStatus(UserStatus.DORMANT)
                .lastLoginAt(LocalDateTime.now())
                .build();

        given(userRepository.findByEmailOrElseThrow(any())).willReturn(user);

        // when & then
        assertThrows(BizException.class, () -> {
            authService.login(new LoginRequest("test@example.com", "password"));
        });

        verify(userRepository, never()).save(any()); // 저장되지 않음
    }

    @Test
    void 로그인성공시_로그인시간_업데이트_및_토큰발급() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded")
                .userStatus(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now().minusMonths(6))
                .build();

        given(userRepository.findByEmailOrElseThrow(any())).willReturn(user);
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(tokenService.generateTokens(any(), any(), any())).willReturn(new TokenResponse("access", "refresh"));

        // when
        TokenResponse response = authService.login(new LoginRequest("test@example.com", "password"));

        // then
        assertNotNull(response);
        verify(userRepository).save(user); // 로그인 시간 업데이트 저장 확인
        assertTrue(user.getLastLoginAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }
}
