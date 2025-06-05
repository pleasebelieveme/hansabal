package org.example.hansabal.domain.dib.service.integration;

import org.example.hansabal.domain.comment.service.DibService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
@Slf4j
@Sql(scripts = {"/user_test_db.sql","/board_test_db.sql","/comment_test_db.sql"},
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DibTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private DibService dibService;

	@Autowired
	private UserService userService;

	@BeforeEach
	void creatUser(){
		UserCreateRequest request = new UserCreateRequest("Test1@email.com","@Aa123456","이름","닉네임", UserRole.USER);
		userService.createUser(request);
	}

	@Test
	void 좋아요_증가_테스트(){

	}
}
