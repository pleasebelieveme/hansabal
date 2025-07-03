package org.example.hansabal.compare;

import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.trade.service.TradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TradeServiceCompareTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
		.withExposedPorts(6379);
	static {
		mysql.start();
		redis.start();
	}
	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () -> mysql.getJdbcUrl()); // ✅ Supplier로 래핑
		registry.add("spring.datasource.username", () -> mysql.getUsername());
		registry.add("spring.datasource.password", () -> mysql.getPassword());
		registry.add("spring.datasource.driver-class-name", () -> mysql.getDriverClassName());

		registry.add("spring.data.redis.host", () -> redis.getHost());
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
	}

	@Autowired
	private TradeService tradeService;
	@Autowired
	private TradeRepository tradeRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeAll
	void setupOnce() {
		System.out.println("🛠️ 테스트 데이터 10,000건 초기화 중...");

		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
		jdbcTemplate.execute("DROP TABLE IF EXISTS trade");
		jdbcTemplate.execute("""
           CREATE TABLE user (
            id BIGINT NOT NULL PRIMARY KEY
              );
       """);
		jdbcTemplate.execute("INSERT INTO user (id) VALUES (1)");

		jdbcTemplate.execute("""
			CREATE TABLE trade (
			    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
			    title VARCHAR(30) NOT NULL,
			    contents TEXT,
			    user_id BIGINT NOT NULL,
			    price BIGINT NOT NULL,
			    is_occupied TINYINT NOT NULL,
			    created_at DATETIME(6),
			    updated_at DATETIME(6),
			    deleted_at DATETIME(6),
			        FOREIGN KEY (user_id) REFERENCES user (id)
			) ENGINE=InnoDB;
			""");

		String sql = "INSERT INTO trade " +
			"(id, title, contents, user_id, price, is_occupied, created_at, updated_at, deleted_at) " +
			"VALUES (?, ?, ?, ?, ?, ?, NOW(6), NOW(6), NULL)";

		for (int i = 1; i <= 10_000; i++) {
			jdbcTemplate.update(sql,
				i,
				"test" + i,
				String.valueOf(i),
				1,
				10000,
				0
			);
		}

		jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
		System.out.println("✅ trade 테이블 초기화 완료 (10,000건)");
	}

	@Test
	void queryLike() {
		// 검색 테스트 예시 (like 검색)
		long start = System.currentTimeMillis();
		Page<TradeResponse> tradesL = tradeService.getTradeListByTitleWithLikeQuery(0,20,"11");
		long end = System.currentTimeMillis();

		long elapsedTime = end - start;
		System.out.println("⏱️ 검색 소요 시간: " + elapsedTime + "ms");

		Assertions.assertFalse(tradesL.isEmpty(), "검색 결과가 비어 있습니다");
	}

	@Test
	void queryMatchAgainst() {
		// 검색 테스트 예시 (like 검색)
		long start = System.currentTimeMillis();
		Page<TradeResponse> trades = tradeService.getTradeListByTitle(0,20,"11");
		long end = System.currentTimeMillis();

		long elapsedTime = end - start;
		System.out.println("⏱️ 검색 소요 시간: " + elapsedTime + "ms");

		Assertions.assertFalse(trades.isEmpty(), "검색 결과가 비어 있습니다");
	}

}

