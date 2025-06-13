package org.example.hansabal.domain.trade.service.intergration;

import static org.assertj.core.api.Assertions.*;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequestDto;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequestDto;
import org.example.hansabal.domain.trade.dto.request.TradeRequestDto;
import org.example.hansabal.domain.trade.dto.response.TradeResponseDto;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Requests;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.trade.repository.RequestsRepository;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"/trade_user_test_db.sql","/trade_test_db.sql","/requests_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
public class TradeTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private TradeService tradeService;
	@Autowired
	private TradeRepository tradeRepository;
	@Autowired
	private RequestsService requestsService;
	@Autowired
	private RequestsRepository requestsRepository;

	@BeforeAll
	public static void beforeAll() {

	}

	@Test
	void 거래_생성() {
		//given
		TradeRequestDto request = new TradeRequestDto("testTitle", "testContents", 5500L);
		UserAuth userAuth = new UserAuth(10L, UserRole.USER);
		//when
		tradeService.createTrade(request, userAuth);
		Trade trade = tradeRepository.findById(8L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		TradeResponseDto response = TradeResponseDto.from(trade);
		//then
		assertThat(response).isNotNull();
		assertThat(response.title()).isEqualTo("testTitle");
		assertThat(response.contents()).isEqualTo("testContents");
		assertThat(response.trader()).isEqualTo(10L);
		assertThat(response.traderNickname()).isEqualTo("testnickname10");
		assertThat(trade.getPrice()).isEqualTo(5500L);
	}

	@Test
	void 거래_수정() {
		UserAuth userAuth = new UserAuth(4L, UserRole.USER);
		TradeRequestDto request = new TradeRequestDto("updatedTitle3", "updatedContents", 9900L);

		tradeService.updateTrade(7L, request, userAuth);
		Trade trade = tradeRepository.findById(7L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		assertThat(trade.getTitle()).isEqualTo("updatedTitle3");
		assertThat(trade.getContents()).isEqualTo("updatedContents");
		assertThat(trade.getPrice()).isEqualTo(9900L);
	}

	@Test
	void 거래_수정_실패_작성자id_불일치() {
		UserAuth userAuth = new UserAuth(5L, UserRole.USER);
		TradeRequestDto request = new TradeRequestDto("updatedTitle3", "updatedContents", 9900L);

		tradeService.updateTrade(7L, request, userAuth);

		assertThatThrownBy(() -> {
			tradeService.updateTrade(7L, request, userAuth);
		})
			.isInstanceOf(BizException.class).hasMessageContaining("본인이 게시한 거래글만 관리할 수 있습니다.");
	}

	@Test
	void 거래_취소() {
		UserAuth userAuth = new UserAuth(4L, UserRole.USER);
		Long tradeId = 7L;

		tradeService.cancelTrade(tradeId, userAuth);
		Trade trade = tradeRepository.findById(7L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		assertThat(trade.getDeletedAt()).isNotNull();
	}

	@Test
	void 거래_요청_등록() {
	UserAuth userAuth = new UserAuth(1L, UserRole.USER);
	RequestsRequestDto request = RequestsRequestDto.builder().tradeId(6L).build();

	requestsService.createRequests(userAuth,request);
	Requests requests = requestsRepository.findById(8L).orElseThrow(() -> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

	assertThat(requests.getStatus()).isEqualTo(RequestStatus.AVAILABLE);
	assertThat(requests.getTrade().getId()).isEqualTo(6L);
	assertThat(requests.getRequester().getId()).isEqualTo(1L);
	}

	@Test
	void 거래_요청_등록_실패_없는_거래_id(){
		UserAuth userAuth = new UserAuth(1L, UserRole.USER);
		RequestsRequestDto request = RequestsRequestDto.builder().tradeId(9L).build();

		requestsService.createRequests(userAuth,request);

		assertThatThrownBy(() -> {
			requestsService.createRequests(userAuth,request);
		})
			.isInstanceOf(BizException.class).hasMessageContaining("해당하는 거래요청을 찾을 수 없습니다.");
	}

	@Test
	void 거래_등록자에_의한_상태_업데이트(){//1번 rq사용
		UserAuth userAuth = new UserAuth(1L,UserRole.USER);
		Long requestsId=1L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.PENDING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		Requests requests = requestsRepository.findById(1L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.PENDING);
	}

	@Test
	void 거래_상태_업데이트_실패_등록자_id_불일치(){//3번rq사용, tid=2
		UserAuth userAuth = new UserAuth(1L,UserRole.USER);
		Long requestsId=3L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.SHIPPING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("사용 권한이 없거나 부족합니다.");
	}

	@Test
	void 거래_상태_업데이트_실패_잘못된_상태_업데이트(){//3번 rq사용, st:paid/done
		UserAuth userAuth = new UserAuth(2L,UserRole.USER);
		Long requestsId=3L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.DONE);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("올바르지 않은 상태값입니다.");
	}

	@Test
	void 거래_상태_업데이트_실패_점유된_거래의_추가_요청_수락_시도(){
		UserAuth userAuth = new UserAuth(1L,UserRole.USER);
		Long requestsId=7L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.PENDING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 요청을 수락한 거래입니다.");
	}

	@Test
	void 거래_상태_업데이트_실패_지불된_요청을_지불_전으로_수정(){//rq3
		UserAuth userAuth = new UserAuth(2L,UserRole.USER);
		Long requestsId=3L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.PENDING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 지불된 요청을 지불 전으로 돌릴 수 없습니다.");
	}

	@Test
	void 거래_요청_지불기능(){//2번 rq 사용 rt6
		UserAuth userAuth = new UserAuth(6L,UserRole.USER);
		Long requestsId=2L;

		requestsService.payTradeFee(requestsId,userAuth);
		Requests requests = requestsRepository.findById(2L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.PAID);
	}

	@Test
	void 거래_요청_완료(){//4번 rq 사용 rt8
		UserAuth userAuth = new UserAuth(8L,UserRole.USER);
		Long requestsId=4L;

		requestsService.confirmGoods(requestsId,userAuth);
		Requests requests = requestsRepository.findById(4L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		Trade trade = tradeRepository.findById(8L).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.DONE);
		assertThat(trade.getDeletedAt()).isNotNull();
	}

	@Test
	void 무료가_아닌_거래_요청_상태_잘못된_업데이트(){//6번 rq 사용 rt9 td4
		UserAuth userAuth = new UserAuth(4L,UserRole.USER);
		Long requestsId=6L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.SHIPPING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("무료가 아닌 거래는 바로 배송단계로 넘길 수 없습니다.");
	}

	@Test
	void 완료된_거래_상태_변경_시도(){//5번 rq 사용 rt9 td4
		UserAuth userAuth = new UserAuth(4L,UserRole.USER);
		Long requestsId=5L;
		RequestsStatusRequestDto request= new RequestsStatusRequestDto(RequestStatus.PENDING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 완료된 거래입니다.");
	}

	@Test
	void 거래요청_취소(){
		UserAuth userAuth = new UserAuth(9L,UserRole.USER);
		Long requestsId=6L;

		requestsService.cancelRequests(requestsId, userAuth);
		Requests requests = requestsRepository.findById(6L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getDeletedAt()).isNotNull();
	}
}
