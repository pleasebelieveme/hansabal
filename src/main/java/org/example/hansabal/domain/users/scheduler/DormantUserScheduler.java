package org.example.hansabal.domain.users.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DormantUserScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    @Transactional
    public void markDormantUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1); // 1년 비로그인 기준

        List<User> dormantUsers = userRepository.findAllByLastLoginAtBeforeAndUserStatus(
                threshold, UserStatus.ACTIVE
        );

        for (User user : dormantUsers) {
            user.markAsDormant();
        }

        log.info("휴면 계정 {}건 처리 완료", dormantUsers.size());
    }
}