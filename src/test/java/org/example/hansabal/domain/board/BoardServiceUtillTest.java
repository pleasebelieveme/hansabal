package org.example.hansabal.domain.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.hansabal.domain.board.entity.BoardCategory.DAILY;
import static org.example.hansabal.domain.users.entity.QUser.user;
import static org.example.hansabal.domain.users.entity.UserRole.USER;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.board.service.BoardServiceUtill;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.users.service.UserService;
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
public class BoardServiceUtillTest {

    @Autowired
    private BoardServiceUtill boardServiceUtill;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;



    @BeforeEach
    @Transactional
    void setup() {
        for (int i = 0; i < 10; i++) {
            String email = "user" + i + "@exmaple.com";
            String nickname = "nickname" + i;

            UserCreateRequest request = new UserCreateRequest(
                    email,
                    "@Aa123456",
                    "테스트이름",
                    nickname,
                    UserRole.USER
            );

            userService.createUser(request);
            }
        // 유저 조회 후 UserAuth 생성
            UserAuth userAuth = new UserAuth(1L, UserRole.USER, "nickname0");
            BoardRequest boardRequest = new BoardRequest(
                   BoardCategory.DAILY,
                    "테스트 제목",
                    "테스트 내용"
            );
            boardService.createPost(userAuth, boardRequest);



    }
    @Test
    @DisplayName("동시 요청에서도 조회수가 정확히 증가해야 한다")
    void viewCountShouldIncreaseCorrectlyUnderConcurrency() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    barrier.await(); // 모든 스레드 동시에 시작
                    boardServiceUtill.viewCount(1L); // 분산락 적용 메서드
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 종료 대기
        executor.shutdown();

        Board result = boardRepository.findById(1L).orElseThrow();

        // 🔍 최종 조회수 검증
        System.out.println("최종 viewCount: " + result.getViewCount());
        assertThat(result.getViewCount()).isEqualTo(threadCount); // 손실 없어야 통과
    }

}
