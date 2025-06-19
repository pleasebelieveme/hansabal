package org.example.hansabal.domain.batch.config;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.entity.ProductOrderStatDaily;
import org.example.hansabal.domain.admin.entity.ProductOrderStatMonthly;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatMonthly;
import org.example.hansabal.domain.order.entity.Order;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import static org.example.hansabal.domain.batch.config.FirstDayOfMonthDecider.*;

/**
 * 상점 주문 통계 배치 작업 설정 클래스
 * 일일/월별 상품 통계 및 관리자 통계를 생성하는 배치 잡을 구성합니다.
 */
@RequiredArgsConstructor
@Configuration
public class ProductStatBatchConfig {

	/**
	 * 상점 주문 통계 배치 잡 정의
	 * 실행 순서:
	 * 1. 상품 일일 통계 생성 (productStatDailyStep)
	 * 2. 관리자 상품 일일 통계 생성 (AdminProductOrderStatDailyStep)
	 * 3. 월 첫날 여부 판단 (firstDayOfMonthDecider)
	 * 4. 월 첫날이면 월별 통계 생성, 아니면 종료
	 */
	@Bean
	public Job ProductOrderStatJob(
			JobRepository jobRepository,
			Step productStatDailyStep,
			Step AdminProductOrderStatDailyStep,
			Step productOrderStatMonthlyStep,
			Step AdminProductOrderStatMonthlyStep,
			FirstDayOfMonthDecider firstDayOfMonthDecider
	) {
		return new JobBuilder("ProductOrderStatJob", jobRepository)
				.start(productStatDailyStep)                    // 상품 일일 통계 생성
				.next(AdminProductOrderStatDailyStep)                // 관리자 상품 일일 통계 생성
				.next(firstDayOfMonthDecider)                   // 월 첫날 여부 판단
				.on(FIRST_DAY)                                  // 월 첫날인 경우
				.to(productOrderStatMonthlyStep)                     // 상품 월별 통계 생성
				.next(AdminProductOrderStatMonthlyStep)              // 관리자 상품 월별 통계 생성
				.from(firstDayOfMonthDecider)
				.on(NOT_FIRST_DAY)                              // 월 첫날이 아닌 경우
				.end()                                          // 배치 종료
				.end()
				.build();
	}

	/**
	 * 상품 일일 통계 생성 스텝
	 * 주문 데이터(Order)를 읽어서 상품별 일일 통계(ProductStatDaily)를 생성합니다.
	 * - 청크 크기: 1000개
	 * - 오류 발생 시 최대 3회 재시도
	 * - 최대 5개 아이템 스킵 허용
	 */
	@Bean
	public Step productStatDailyStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			ItemReader<Order> reader,                           // 주문 데이터 읽기
			ItemProcessor<Order, ProductOrderStatDaily> processor,   // 주문을 상품 일일 통계로 변환
			ItemWriter<ProductOrderStatDaily> writer                 // 상품 일일 통계 저장
	) {
		return new StepBuilder("productStatDailyStep", jobRepository)
				.<Order, ProductOrderStatDaily>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()                                // 오류 허용 설정
				.retry(Exception.class)                         // 모든 예외에 대해 재시도
				.retryLimit(3)                                  // 최대 3회 재시도
				.backOffPolicy(exponentialBackOffPolicy())      // 지수 백오프 정책 적용
				.skip(Exception.class)                          // 모든 예외에 대해 스킵 허용
				.skipLimit(5)                                   // 최대 5개 아이템 스킵
				.build();
	}

	/**
	 * 관리자 상품 일일 통계 생성 스텝
	 * 상품 일일 통계(ProductStatDaily)를 읽어서 관리자용 상품 일일 통계(AdminProductOrderStatDaily)를 생성합니다.
	 * - 청크 크기: 100개
	 * - 오류 발생 시 최대 3회 재시도
	 * - 최대 1개 아이템 스킵 허용
	 */
	@Bean
	public Step AdminProductOrderStatDailyStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			@Qualifier("adminProductOrderStatDailyReader")
			ItemReader<ProductOrderStatDaily> reader,                              // 상품 일일 통계 읽기
			ItemProcessor<ProductOrderStatDaily, AdminProductOrderStatDaily> processor, // 관리자 상품 일일 통계로 변환
			ItemWriter<AdminProductOrderStatDaily> writer                          // 관리자 상품 일일 통계 저장
	) {
		return new StepBuilder("AdminProductOrderStatDailyStep", jobRepository)
				.<ProductOrderStatDaily, AdminProductOrderStatDaily>chunk(100, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()                                // 오류 허용 설정
				.retry(Exception.class)                         // 모든 예외에 대해 재시도
				.retryLimit(3)                                  // 최대 3회 재시도
				.backOffPolicy(exponentialBackOffPolicy())      // 지수 백오프 정책 적용
				.skip(Exception.class)                          // 모든 예외에 대해 스킵 허용
				.skipLimit(1)                                   // 최대 1개 아이템 스킵
				.build();
	}

	/**
	 * 상품 월별 통계 생성 스텝 (월 첫날에만 실행)
	 * 상품 일일 통계(ProductStatDaily)를 읽어서 상품별 월별 통계(ProductStatMonthly)를 생성합니다.
	 * - 청크 크기: 100개
	 * - 오류 발생 시 최대 3회 재시도
	 * - 최대 1개 아이템 스킵 허용
	 */
	@Bean
	public Step productOrderStatMonthlyStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			@Qualifier("productOrderStatMonthlyReader")
			ItemReader<ProductOrderStatDaily> reader,                          // 상품 일일 통계 읽기
			ItemProcessor<ProductOrderStatDaily, ProductOrderStatMonthly> processor, // 상품 월별 통계로 변환
			ItemWriter<ProductOrderStatMonthly> writer                         // 상품 월별 통계 저장
	) {
		return new StepBuilder("productOrderStatMonthlyStep", jobRepository)
				.<ProductOrderStatDaily, ProductOrderStatMonthly>chunk(100, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()                                // 오류 허용 설정
				.retry(Exception.class)                         // 모든 예외에 대해 재시도
				.retryLimit(3)                                  // 최대 3회 재시도
				.backOffPolicy(exponentialBackOffPolicy())      // 지수 백오프 정책 적용
				.skip(Exception.class)                          // 모든 예외에 대해 스킵 허용
				.skipLimit(1)                                   // 최대 1개 아이템 스킵
				.build();
	}

	/**
	 * 관리자 상품 월별 통계 생성 스텝 (월 첫날에만 실행)
	 * 상품 월별 통계(ProductStatMonthly)를 읽어서 관리자용 상품 월별 통계(AdminProductOrderStatMonthly)를 생성합니다.
	 * - 청크 크기: 100개
	 * - 오류 발생 시 최대 3회 재시도
	 * - 최대 1개 아이템 스킵 허용
	 */
	@Bean
	public Step AdminProductOrderStatMonthlyStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			@Qualifier("adminProductOrderStatMonthlyReader")
			ItemReader<ProductOrderStatMonthly> reader,                              // 상품 월별 통계 읽기
			ItemProcessor<ProductOrderStatMonthly, AdminProductOrderStatMonthly> processor, // 관리자 상품 월별 통계로 변환
			ItemWriter<AdminProductOrderStatMonthly> writer                          // 관리자 상품 월별 통계 저장
	) {
		return new StepBuilder("AdminProductOrderStatMonthlyStep", jobRepository)
				.<ProductOrderStatMonthly, AdminProductOrderStatMonthly>chunk(100, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()                                // 오류 허용 설정
				.retry(Exception.class)                         // 모든 예외에 대해 재시도
				.retryLimit(3)                                  // 최대 3회 재시도
				.backOffPolicy(exponentialBackOffPolicy())      // 지수 백오프 정책 적용
				.skip(Exception.class)                          // 모든 예외에 대해 스킵 허용
				.skipLimit(1)                                   // 최대 1개 아이템 스킵
				.build();
	}

	/**
	 * 지수 백오프 정책 설정
	 * 재시도 간격을 점진적으로 늘려가는 정책을 정의합니다.
	 * - 초기 간격: 1초 (1000ms)
	 * - 배수: 2.0 (매번 2배씩 증가)
	 * - 최대 간격: 30초 (30000ms)
	 */
	@Bean
	public ExponentialBackOffPolicy exponentialBackOffPolicy() {
		ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setInitialInterval(1000L);    // 초기 재시도 간격: 1초
		policy.setMultiplier(2.0);           // 재시도 시마다 간격을 2배로 증가
		policy.setMaxInterval(30000L);       // 최대 재시도 간격: 30초
		return policy;
	}
}