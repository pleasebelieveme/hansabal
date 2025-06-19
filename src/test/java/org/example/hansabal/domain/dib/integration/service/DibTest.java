package org.example.hansabal.domain.dib.integration.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Slf4j
@Sql(scripts = "/comment_board_test_db.sql",
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
	void 좋아요_증가_및_감소_테스트() throws InterruptedException{
		int threadCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(threadCount);
		CyclicBarrier barrier = new CyclicBarrier(threadCount);
		DibRequest request = new DibRequest(DibType.BOARD,1L);

		AtomicInteger increaseCount = new AtomicInteger();

		for(int i = 0; i < threadCount; i++){
			long userId = i+1L;
			executor.execute( () -> {
				try {
					barrier.await();
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

		assertThat(increaseCount.get()).isEqualTo(100);
		assertThat(board.getDibCount()).isEqualTo(100);
		assertThat(dibs).hasSize(100);

		log.info("좋아요 최종 수: {}", board.getDibCount());
		log.info("성공 요청 수: {}", increaseCount.get());

		AtomicInteger cancelCount = new AtomicInteger();
		CyclicBarrier cancelBarrier = new CyclicBarrier(threadCount);
		CountDownLatch cancelLatch = new CountDownLatch(threadCount);

		Thread.sleep(10000);

		executor = Executors.newFixedThreadPool(100);

		for (int i = 0; i < threadCount; i++) {
			long userId = i + 1L;
			executor.submit(() -> {
				try {
					cancelBarrier.await();
					dibService.modifyDibs(userId, request);
					cancelCount.incrementAndGet();
				} catch (BizException e){
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cancelLatch.countDown();
				}
			});
		}

		cancelLatch.await();
		executor.shutdown();

		Board boardAfterCancel = boardRepository.findById(1L).orElseThrow();
		List<Dib> dibsAfterCancel = dibRepository.findAll();

		assertThat(boardAfterCancel.getDibCount()).isEqualTo(0);
		assertThat(dibsAfterCancel).hasSize(0);

		log.info("취소 후 좋아요 수: {}", boardAfterCancel.getDibCount());
		log.info("취소 성공 수: {}", cancelCount.get());
	}

}
