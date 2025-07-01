package org.example.hansabal.domain.batch.config;

import java.time.LocalDate;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
public class FirstDayOfMonthDecider implements JobExecutionDecider {

	// ℹ️ 상수로 상태 값을 정의하여 분기 조건을 문자열로 표현 (Step 간 흐름 제어용)
	public static final String FIRST_DAY = "FIRST_DAY";
	public static final String NOT_FIRST_DAY = "NOT_FIRST_DAY";

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		// ✅ 수정 검토: 현재는 시스템 날짜(LocalDate.now()) 기준으로 판단함.
		// 만약 배치 실행 기준일이 JobParameter 등 외부 입력이라면 LocalDate.now() 대신 해당 파라미터를 사용해야 더 유연함.
		// 예시:
		// String dateParam = jobExecution.getJobParameters().getString("targetDate");
		// LocalDate targetDate = LocalDate.parse(dateParam);
		// return targetDate.getDayOfMonth() == 1 ? new FlowExecutionStatus(FIRST_DAY) : new FlowExecutionStatus(NOT_FIRST_DAY);

		return LocalDate.now().getDayOfMonth() == 1
				? new FlowExecutionStatus(FIRST_DAY)
				: new FlowExecutionStatus(NOT_FIRST_DAY);
	}
}
