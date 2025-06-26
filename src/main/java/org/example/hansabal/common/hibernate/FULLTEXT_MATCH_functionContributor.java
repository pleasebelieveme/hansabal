package org.example.hansabal.common.hibernate;

import static org.hibernate.type.StandardBasicTypes.*;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class FULLTEXT_MATCH_functionContributor implements FunctionContributor {

	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {//match()against() 커스텀함수 정의
		functionContributions
			.getFunctionRegistry()
			.registerPattern("fulltext_match", "match(?1) against(?2 in boolean mode)",
				functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));
	}
}
