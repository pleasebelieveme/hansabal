package org.example.hansabal.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {
	private final EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory(){
		return new JPAQueryFactory(entityManager);
	}
}
