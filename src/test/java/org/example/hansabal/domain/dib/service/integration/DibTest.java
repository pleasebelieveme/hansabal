package org.example.hansabal.domain.dib.service.integration;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.comment.dto.request.DibRequest;
import org.example.hansabal.domain.comment.entity.Dib;
import org.example.hansabal.domain.comment.entity.DibType;
import org.example.hansabal.domain.comment.repository.DibRepository;
import org.example.hansabal.domain.comment.service.DibService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Slf4j
@Sql(scripts = "/board_test_db.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DibTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private DibService dibService;

	@Autowired
	private UserService userService;

	@Autowired
	private DibRepository dibRepository;

	@Autowired
	private BoardRepository boardRepository;

	@BeforeAll
	void creatUser(){

		for(int i = 0; i < 1000; i++){
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
	}

	@Test
	void 좋아요_증가_테스트() throws InterruptedException{
		int threadCount = 1000;
		ExecutorService executor = Executors.newFixedThreadPool(50);
		CountDownLatch latch = new CountDownLatch(threadCount);
		// CyclicBarrier barrier = new CyclicBarrier(threadCount);
		DibRequest request = new DibRequest(DibType.BOARD,1L);

		AtomicInteger increaseCount = new AtomicInteger();

		for(int i = 0; i < threadCount; i++){
			long userId = i+1L;
			executor.submit( () -> {
				try {
					// barrier.await();
					dibService.modifyDibs(userId,request);
					increaseCount.incrementAndGet();
				} catch (BizException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		Board board = boardRepository.findById(1L).orElseThrow();
		List<Dib> dibs = dibRepository.findAll();

		assertThat(increaseCount.get()).isEqualTo(1000);
		assertThat(board.getDibCount()).isEqualTo(1000);
		assertThat(dibs).hasSize(1000);

		log.info("좋아요 최종 수: {}", board.getDibCount());
		log.info("성공 요청 수: {}", increaseCount.get());
	}
}
