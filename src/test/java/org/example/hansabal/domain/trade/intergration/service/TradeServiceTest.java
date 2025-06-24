package org.example.hansabal.domain.trade.intergration.service;

import static org.assertj.core.api.Assertions.*;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequest;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequest;
import org.example.hansabal.domain.trade.dto.request.TradeRequest;
import org.example.hansabal.domain.trade.dto.response.TradeResponse;
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
import org.junit.jupiter.api.DisplayName;
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
public class TradeServiceTest {

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
	@DisplayName("거래_생성")
	void tradePostTest() {
		//given
		TradeRequest request = new TradeRequest("testTitle", "testContents", 5500L);
		UserAuth userAuth = new UserAuth(10L, UserRole.USER,"testnickname10");
		//when
		tradeService.createTrade(request, userAuth);
		Trade trade = tradeRepository.findById(9L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));
		TradeResponse response = TradeResponse.from(trade);
		//then
		assertThat(response).isNotNull();
		assertThat(response.title()).isEqualTo("testTitle");
		assertThat(response.contents()).isEqualTo("testContents");
		assertThat(response.trader()).isEqualTo(10L);
		assertThat(response.traderNickname()).isEqualTo("testnickname10");
		assertThat(trade.getPrice()).isEqualTo(5500L);
	}

	@Test
	@DisplayName("거래_수정")
	void tradeUpdateTest() {
		UserAuth userAuth = new UserAuth(4L, UserRole.USER,"testnickname4");
		TradeRequest request = new TradeRequest("updatedTitle3", "updatedContents", 9900L);

		tradeService.updateTrade(7L, request, userAuth);
		Trade trade = tradeRepository.findById(7L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		assertThat(trade.getTitle()).isEqualTo("updatedTitle3");
		assertThat(trade.getContents()).isEqualTo("updatedContents");
		assertThat(trade.getPrice()).isEqualTo(9900L);
	}

	@Test//고장
	@DisplayName("거래_수정_실패_작성자id_불일치")
	void tradeUpdateTestFailByWrongId() {
		UserAuth userAuth = new UserAuth(5L, UserRole.USER,"testnickname5");
		TradeRequest request = new TradeRequest("updatedTitle3", "updatedContents", 9900L);

		assertThatThrownBy(() -> {
			tradeService.updateTrade(7L, request, userAuth);
		})
			.isInstanceOf(BizException.class).hasMessageContaining("본인이 게시한 거래글만 관리할 수 있습니다.");
	}

	@Test
	@DisplayName("거래_취소")
	void tradeCancelTest() {
		UserAuth userAuth = new UserAuth(4L, UserRole.USER,"testnickname4");
		Long tradeId = 8L;

		tradeService.cancelTrade(tradeId, userAuth);
		Trade trade = tradeRepository.findById(7L).orElseThrow(() -> new BizException(TradeErrorCode.TRADE_NOT_FOUND));

		assertThat(trade.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("거래_요청_등록")
	void requestsPostTest() {
	UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");
	RequestsRequest request = RequestsRequest.builder().tradeId(6L).build();

	requestsService.createRequests(userAuth,request);
	Requests requests = requestsRepository.findById(9L).orElseThrow(() -> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

	assertThat(requests.getStatus()).isEqualTo(RequestStatus.AVAILABLE);
	assertThat(requests.getTrade().getId()).isEqualTo(6L);
	assertThat(requests.getRequester().getId()).isEqualTo(1L);
	}

	@Test//고장
	@DisplayName("거래_요청_등록_실패_없는_거래_id")
	void requestsPostTestFailByTradeIdNotExist(){
		UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");
		RequestsRequest request = RequestsRequest.builder().tradeId(10L).build();

		assertThatThrownBy(() -> {requestsService.createRequests(userAuth,request);
		}).isInstanceOf(BizException.class).hasMessageContaining("해당하는 거래요청을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("거래_등록자에_의한_상태_업데이트")
	void requestsStatusUpdateByTraderTest(){//1번 rq사용
		UserAuth userAuth = new UserAuth(1L,UserRole.USER,"testnickname1");
		Long requestsId=1L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.PENDING);

		requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		Requests requests = requestsRepository.findById(1L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.PENDING);
	}

	@Test//고장
	@DisplayName("거래_상태_업데이트_실패_등록자_id_불일치")
	void requestsStatusUpdateTestFailByWrongId(){//3번rq사용, tid=2
		UserAuth userAuth = new UserAuth(1L,UserRole.USER,"testnickname1");
		Long requestsId=3L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.SHIPPING);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("사용 권한이 없거나 부족합니다.");
	}

	@Test//고장
	@DisplayName("거래_상태_업데이트_실패_잘못된_상태_업데이트")
	void requestsUpdateTestFailByWrongStatusCase1(){//3번 rq사용, st:paid/done
		UserAuth userAuth = new UserAuth(2L,UserRole.USER,"testnickname2");
		Long requestsId=3L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.DONE);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("올바르지 않은 상태값입니다.");
	}

	@Test
	@DisplayName("거래_상태_업데이트_실패_점유된_거래의_추가_요청_수락_시도")
	void requestsStatusUpdateTestFailByOccupancy(){
		UserAuth userAuth = new UserAuth(4L,UserRole.USER,"testnickname4");
		Long requestsId=7L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.PENDING);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 요청을 수락한 거래입니다.");
	}

	@Test
	@DisplayName("거래_상태_업데이트_실패_지불된_요청을_지불_전으로_수정")
	void requestsUpdateTestFailByWrongStatusCase2(){//rq3
		UserAuth userAuth = new UserAuth(2L,UserRole.USER,"testnickname2");
		Long requestsId=3L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.PENDING);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 지불된 요청을 지불 전으로 돌릴 수 없습니다.");
	}

	@Test
	@DisplayName("거래_요청_지불기능")
	void requestsPayTest(){//2번 rq 사용 rt6
		UserAuth userAuth = new UserAuth(6L,UserRole.USER,"testnickname6");
		Long requestsId=2L;

		requestsService.payTradeFee(requestsId,userAuth);
		Requests requests = requestsRepository.findById(2L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.PAID);
	}

	@Test
	@DisplayName("거래_요청_완료")
	void requestsConfirmTest(){//4번 rq 사용 rt8
		UserAuth userAuth = new UserAuth(8L,UserRole.USER,"testnickname8");
		Long requestsId=4L;

		requestsService.confirmGoods(requestsId,userAuth);
		Requests requests = requestsRepository.findById(4L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));
		Trade trade = tradeRepository.findById(4L).orElseThrow(()-> new BizException(TradeErrorCode.TRADE_NOT_FOUND));//<=여기 고장원인

		assertThat(requests.getStatus()).isEqualTo(RequestStatus.DONE);
		assertThat(trade.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("무료가_아닌_거래_요청_상태_잘못된_업데이트")
	void requestsUpdateTestFailedByStatusError1(){//6번 rq 사용 rt9 td4
		UserAuth userAuth = new UserAuth(4L,UserRole.USER,"testnickname4");
		Long requestsId=6L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.SHIPPING);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("무료가 아닌 거래는 바로 배송단계로 넘길 수 없습니다.");
	}

	@Test
	@DisplayName("완료된_거래_상태_변경_시도")
	void requestsUpdateTestFailedByStatusError2(){//5번 rq 사용 rt9 td4
		UserAuth userAuth = new UserAuth(4L,UserRole.USER,"testnickname4");
		Long requestsId=5L;
		RequestsStatusRequest request= new RequestsStatusRequest(RequestStatus.PENDING);

		assertThatThrownBy(()->{requestsService.updateRequestsByTrader(requestsId, request, userAuth);
		}).isInstanceOf(BizException.class).hasMessageContaining("이미 완료된 거래입니다.");
	}

	@Test
	@DisplayName("거래요청_취소")
	void requestsCancelTest(){
		UserAuth userAuth = new UserAuth(9L,UserRole.USER,"testnickname9");
		Long requestsId=8L;

		requestsService.cancelRequests(requestsId, userAuth);
		Requests requests = requestsRepository.findById(6L).orElseThrow(()-> new BizException(TradeErrorCode.REQUESTS_NOT_FOUND));

		assertThat(requests.getDeletedAt()).isNotNull();
	}
}
