package org.example.hansabal.common.hibernate;

import static org.hibernate.type.StandardBasicTypes.*;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class FULLTEXT_MATCH_functionContributor implements FunctionContributor {
//FunctionContributor -> function에 번호를 부여, 높은번호는 하위번호의 함수를 오버라이드 하여 사용 가능.
	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {//match()against() 커스텀함수 정의
		//hibernate의 functionContributions 객체에
		//"fulltext_match"라는 이름으로 "match(?1) aginst(?2 in boolean mode)" 라는 쿼리패턴을 가진 함수를 등록. 이는 실제 날라가는 쿼리문과 일치합니다.
		// boolean 형 응답처리로 함수 type을 설정.
		functionContributions
			.getFunctionRegistry()
			.registerPattern("fulltext_match", "match(?1) against(?2 in boolean mode)",
				functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));
	}
}
