package org.example.hansabal.domain.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.hansabal.domain.board.entity.BoardCategory.DAILY;
import static org.example.hansabal.domain.users.entity.UserRole.USER;

import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.board.service.BoardServiceUtill;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
@Transactional
public class BoardServiceUtillTest {

    @Autowired
    private BoardServiceUtill boardServiceUtill;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    private Long postId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(User.builder()
                .email("user")
                .password("pw")
                .nickname("test")
                .userRole(USER)
                .build());

        Board board = boardRepository.save(Board.builder()
                .user(user)
                .category(DAILY)
                .title("동시 테스트")
                .content("내용")
                .viewCount(0)
                .dibCount(0)
                .build());

        postId = board.getId();
    }
    @Test
    @DisplayName("동시 요청에서도 조회수가 정확히 증가해야 한다")
    void viewCountShouldIncreaseCorrectlyUnderConcurrency() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    barrier.await(); // 모든 스레드 동시에 시작
                    boardServiceUtill.viewCount(postId); // 분산락 적용 메서드
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 종료 대기
        executor.shutdown();

        Board result = boardRepository.findById(postId).orElseThrow();

        // 🔍 최종 조회수 검증
        System.out.println("최종 viewCount: " + result.getViewCount());
        assertThat(result.getViewCount()).isEqualTo(threadCount); // 손실 없어야 통과
    }

}
