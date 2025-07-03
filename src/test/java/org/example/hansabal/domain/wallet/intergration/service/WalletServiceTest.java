package org.example.hansabal.domain.wallet.intergration.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.example.hansabal.domain.wallet.entity.WalletHistory;
import org.example.hansabal.domain.wallet.exception.WalletErrorCode;
import org.example.hansabal.domain.wallet.repository.WalletHistoryRepository;
import org.example.hansabal.domain.wallet.repository.WalletRepository;
import org.example.hansabal.domain.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"classpath:trade_user_test_db.sql","classpath:trade_test_db.sql","classpath:requests_test_db.sql",
	"classpath:wallet_test_db.sql", "classpath:history_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WalletServiceTest {
	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:6.2")
		.withExposedPorts(6379);

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
	private TradeRepository tradeRepository;
	@Autowired
	private WalletRepository walletRepository;
	@Autowired
	private WalletService walletService;
	@Autowired
	private WalletHistoryRepository walletHistoryRepository;
	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("지갑 생성 테스트")
	void createWalletTest() {//id email password name nickname lastLoginAt userRole userStatus
		User user = User.builder()
			.id(10L)
			.email("test10@email.com")
			.password("!Aa123456")
			.name("testname10")
			.nickname("testnickname10")
			.lastLoginAt(LocalDateTime.now())
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();

		walletService.createWallet(user);
		Wallet wallet = walletRepository.findById(10L).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));

		assertThat(wallet).isNotNull();
		assertThat(wallet.getUser()).isEqualTo(user);
		assertThat(wallet.getCash()).isEqualTo(0L);
	}

	@Test
	@DisplayName("지갑 생성 실패 테스트 - 지갑 중복 생성 오류")
	void createWalletTestFailByDuplicateError() {
		User user = User.builder()
			.id(9L)
			.email("test9@email.com")
			.password("!Aa123456")
			.name("testname9")
			.nickname("testnickname9")
			.lastLoginAt(LocalDateTime.now())
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();

		assertThatThrownBy(()-> {
			walletService.createWallet(user);
		})
			.isInstanceOf(BizException.class).hasMessageContaining("유저당 하나의 전자지갑만 소유할 수 있습니다.");
	}

	@Test
	@DisplayName("비용 지불 테스트")
	void walletPayTest() {//id email password name nickname lastLoginAt userRole userStatus
		User user = User.builder()
			.id(1L)
			.email("test@email.com")
			.password("!Aa123456")
			.name("testname")
			.nickname("testnickname1")
			.lastLoginAt(LocalDateTime.now())
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
		Long tradeId=1L;
		Long price=4900L;

		walletService.walletPay(user, tradeId, price);
		Wallet wallet = walletRepository.findById(1L).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		WalletHistory walletHistory = walletHistoryRepository.findById(3L).orElseThrow(()->new BizException(WalletErrorCode.HISTORY_NOT_EXIST));

		assertThat(wallet.getCash()).isEqualTo(100L);
		assertThat(walletHistory.getType()).isEqualTo("구매");
		assertThat(walletHistory.getTradeId()).isEqualTo(1L);
		assertThat(walletHistory.getPrice()).isEqualTo(4900L);
		assertThat(walletHistory.getRemain()).isEqualTo(100L);
	}

	@Test
	@DisplayName("비용 지불 실패 테스트 - 잔액 부족")
	void walletPayTestFailByNotEnoughCash() {//id email password name nickname lastLoginAt userRole userStatus
		User user = User.builder()
			.id(1L)
			.email("test@email.com")
			.password("!Aa123456")
			.name("testname")
			.nickname("testnickname1")
			.lastLoginAt(LocalDateTime.now())
			.userRole(UserRole.USER)
			.userStatus(UserStatus.ACTIVE)
			.build();
		Long tradeId=1L;
		Long price=5100L;
		assertThatThrownBy(()-> {
			walletService.walletPay(user, tradeId, price);
		}).isInstanceOf(BizException.class).hasMessageContaining("잔액이 부족하여 거래를 진행할 수 없습니다.");

	}

	@Test
	@DisplayName("거래 완료 수익 획득 테스트")
	void walletTradeConfirmTest() {
		Trade trade=tradeRepository.findById(5L).orElseThrow(()->new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		Long requestsId=5L;

		walletService.walletConfirm(trade, requestsId);
		em.flush();
		em.clear();
		Wallet wallet = walletRepository.findById(4L).orElseThrow(()->new BizException(WalletErrorCode.NO_WALLET_FOUND));
		List<WalletHistory> walletHistory = walletHistoryRepository.findAllByTradeId(5L);

		assertThat(wallet.getCash()).isEqualTo(115500L);
		assertThat(walletHistory.get(0).getType()).isEqualTo("판매수익");
		assertThat(walletHistory.get(0).getTradeId()).isEqualTo(5L);
		assertThat(walletHistory.get(0).getPrice()).isEqualTo(-30500L);
		assertThat(walletHistory.get(0).getRemain()).isEqualTo(115500L);
	}
}
