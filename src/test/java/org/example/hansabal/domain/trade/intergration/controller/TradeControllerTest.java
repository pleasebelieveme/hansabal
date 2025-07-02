package org.example.hansabal.domain.trade.intergration.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.trade.dto.request.RequestsRequest;
import org.example.hansabal.domain.trade.dto.request.RequestsStatusRequest;
import org.example.hansabal.domain.trade.dto.request.TradeRequest;
import org.example.hansabal.domain.trade.dto.response.RequestsResponse;
import org.example.hansabal.domain.trade.dto.response.TradeResponse;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.service.RequestsService;
import org.example.hansabal.domain.trade.service.TradeService;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = {"/trade_user_test_db.sql", "/trade_test_db.sql", "/requests_test_db.sql"}
	,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TradeControllerTest {
	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
		registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
	}

	// 테스트 클래스에서 DI로 MockMvc를 주입받아 실제 HTTP 요청처럼 시뮬레이션 가능
	@Autowired
	MockMvc mockMvc;

	// JSON 직렬화/역직렬화를 위한 Jackson 라이브러리의 ObjectMapper 주입
	@Autowired
	ObjectMapper objectMapper;

	// @SpringBootTest 환경에서는 @MockBean이나 @MockitoBean으로 Service를 가짜로 주입 가능
	// @MockitoBean은 Spring 6.2+의 새로운 방식 (기존 @MockBean 대체)
	@MockitoBean
	TradeService tradeService;
	@MockitoBean
	RequestsService requestsService;

	@Test
	@DisplayName("거래 생성 테스트")
	void createTradeTest()throws Exception{
		TradeRequest request = new TradeRequest("testTitle", "testContents",5500L);
		TradeResponse response = new TradeResponse(9L, "testTitle", "testContents", 1L, 5500L, "testnickname1");

		Mockito.when(tradeService.createTrade(any(),any())).thenReturn(response);

		mockMvc.perform(post("/api/trade").with(user("1").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("testTitle"))
			.andExpect(jsonPath("$.contents").value("testContents"))
			.andExpect(jsonPath("$.traderNickname").value("testNickname1"));
	}

	@Test
	@DisplayName("거래 수정 테스트")
	void updateTradeTest() throws Exception{
		Long tradeId=1L;
		TradeRequest request = new TradeRequest("updatedTitle","updatedContents",25000L);
		TradeResponse response = new TradeResponse(1L,"updatedTitle","updatedContents",1L, 25000L,"testnickname1");

		Mockito.when(tradeService.updateTrade(eq(1L),request,any())).thenReturn(response);

		mockMvc.perform(patch("/api/trade/{tradeId}",tradeId)
				.with(user("1").roles("USER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("updatedTitle"))
			.andExpect(jsonPath("$.contents").value("updatedContents"));
	}

	@Test
	@DisplayName("거래 취소 테스트")
	void deleteTradeTest() throws Exception{
		Long tradeId=1L;
		UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

		mockMvc.perform(delete("/api/trade/{tradeId}",tradeId)
				.with(user("1").roles("USER")))
			.andExpect(status().isOk());

		verify(tradeService).cancelTrade(tradeId, userAuth);
	}

	@Test
	@DisplayName("거래 요청 생성 테스트")
	void createRequestsTest() throws Exception{
		RequestsRequest request = new RequestsRequest(2L);
		RequestsResponse response = new RequestsResponse(9L, RequestStatus.AVAILABLE, 2L, 3L);

		Mockito.when(requestsService.createRequests(any(),any())).thenReturn(response);

		mockMvc.perform(post("/api/trade/requests/")
			.with(user("3").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.requestsId").value("9L"))
			.andExpect(jsonPath("$.status").value("AVAILABLE"))
			.andExpect(jsonPath("$.tradeId").value("2L"))
			.andExpect(jsonPath("$.requesterId").value("3L"));
	}

	@Test
	@DisplayName("거래 요청 상태 업데이트 테스트")
	void updateRequestsTest() throws Exception{
		UserAuth userAuth = new UserAuth(9L, UserRole.USER,"testnickname9");
		Long requestsId = 6L;
		RequestsStatusRequest request = RequestsStatusRequest.builder()
			.requestStatus(RequestStatus.PENDING)
			.build();
		RequestsResponse response = new RequestsResponse(6L, RequestStatus.PENDING, 6L, 9L);

		Mockito.when(requestsService.updateRequestsByTrader(requestsId, request, userAuth)).thenReturn(response);

		mockMvc.perform(patch("/api/trade/requests/{requestsId}",requestsId)
			.with(user("9").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.requestsId").value("6L"))
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andExpect(jsonPath("$.tradeId").value("6L"))
			.andExpect(jsonPath("$.requesterId").value("9L"));
	}

	@Test
	@DisplayName("거래 요청 취소 테스트")
	void cancelRequestsTest() throws Exception{
		Long requestsId = 7L;
		UserAuth userAuth = new UserAuth(5L, UserRole.USER,"testnickname5");

		mockMvc.perform(delete("/api/trade/requests/{requestsId}",requestsId)
			.with(user("5").roles("USER")))
			.andExpect(status().isOk());

		verify(requestsService).cancelRequests(requestsId,userAuth);
	}

	@Test
	@DisplayName("거래 요청 지불 테스트")
	void requestsPayTest() throws Exception{
		Long requestsId = 2L;
		UserAuth userAuth = new UserAuth(6L, UserRole.USER, "testnickname6");
		RequestsResponse response = new RequestsResponse(2L, RequestStatus.PAID, 2L, 6L);

		Mockito.when(requestsService.payTradeFee(requestsId, userAuth)).thenReturn(response);

		mockMvc.perform(patch("/api/trade/requests/{requestsId}/pay", requestsId)
			.with(user("6").roles("USER"))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.requestsId").value("2L"))
			.andExpect(jsonPath("$.status").value("PAID"))
			.andExpect(jsonPath("$.tradeId").value("2L"))
			.andExpect(jsonPath("$.requesterId").value("6L"));
	}

	@Test
	@DisplayName("거래 요청 완료 처리 테스트")
	void requestsCompletionTest() throws Exception{
		Long requestsId = 4L;
		UserAuth userAuth = new UserAuth(8L, UserRole.USER, "testnickname8");
		RequestsResponse response = new RequestsResponse(4L, RequestStatus.DONE, 4L, 8L);

		Mockito.when(requestsService.confirmGoods(requestsId, userAuth)).thenReturn(response);

		mockMvc.perform(patch("/api/trade/requests/{requestsId}/confirm", requestsId)
				.with(user("8").roles("USER"))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.requestsId").value("4L"))
			.andExpect(jsonPath("$.status").value("DONE"))
			.andExpect(jsonPath("$.tradeId").value("4L"))
			.andExpect(jsonPath("$.requesterId").value("8L"));
	}
}
