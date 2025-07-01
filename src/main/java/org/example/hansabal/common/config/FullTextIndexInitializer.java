package org.example.hansabal.common.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FullTextIndexInitializer {

	private final JdbcTemplate jdbcTemplate;

	@EventListener(ApplicationReadyEvent.class)
	public void applyFullTextIndex(){
		try {
			jdbcTemplate.execute("ALTER TABLE trade ADD FULLTEXT(title)");
			jdbcTemplate.execute("ALTER TABLE boards ADD FULLTEXT(title)");
			jdbcTemplate.execute("ALTER TABLE boards ADD FULLTEXT(content)");
			System.out.println("✅ FULLTEXT 인덱스 적용 완료");
		} catch (Exception e) {
			System.err.println("❌ 인덱스 생성 실패: " + e.getMessage());
		}
	}
}
