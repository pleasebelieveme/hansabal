package org.example.hansabal.domain.users.service.integration;

import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.users.scheduler.DormantUserScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class DormantUserSchedulerTest {

    @Autowired
    private DormantUserScheduler dormantUserScheduler;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testMarkDormantUsers() {
        // given: 6개월 전 로그인 기록을 가진 유저 생성
        User user = User.builder()
                .email("test@example.com")
                .password("encoded-pw")
                .name("testname")
                .nickname("testnickname")
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now().minusMonths(7)) // 기준점보다 예전
                .build();

        userRepository.save(user);

        // when: 스케줄러 실행
        dormantUserScheduler.markDormantUsers();

        // then: 유저의 상태가 DORMANT로 바뀌었는지 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.DORMANT);
    }
}
