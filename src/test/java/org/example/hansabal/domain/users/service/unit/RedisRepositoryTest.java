package org.example.hansabal.domain.users.service.unit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisRepository redisRepository;

    @Test
    void validateKey_예외발생시_BizException던짐() {
        // given
        String token = "testAccessToken";

        when(redisTemplate.hasKey(anyString()))
                .thenThrow(new RuntimeException("Redis 오류"));

        // when & then
        assertThatThrownBy(() -> redisRepository.validateKey(token))
                .isInstanceOf(BizException.class);
    }

    @Test
    void saveBlackListToken_예외발생시_BizException던짐() {
        // given
        String token = "testAccessToken";
        long expirationMillis = 1000L;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Redis 오류"))
                .when(valueOperations)
                .set(anyString(), anyString(), anyLong(), any());

        // when & then
        assertThatThrownBy(() -> redisRepository.saveBlackListToken(token, expirationMillis))
                .isInstanceOf(BizException.class);
    }

    @Test
    void saveRefreshToken_예외발생시_BizException던짐() {
        // given
        Long userId = 1L;
        String refreshToken = "testRefreshToken";
        long expirationMillis = 1000L;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Redis 오류"))
                .when(valueOperations)
                .set(anyString(), anyString(), anyLong(), any());

        // when & then
        assertThatThrownBy(() -> redisRepository.saveRefreshToken(userId, refreshToken, expirationMillis))
                .isInstanceOf(BizException.class);
    }
}
