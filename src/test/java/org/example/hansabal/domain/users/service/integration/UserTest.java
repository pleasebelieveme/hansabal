package org.example.hansabal.domain.users.service.integration;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.users.service.UserService;
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

import java.util.Optional;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"/comment_user_test_db.sql"}
        ,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
public class UserTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @BeforeAll
    public static void beforeAll() {
        // 테스트 전체 실행 전에 필요한 설정이 있다면 여기에 작성
    }

    @Test
    void 유저_생성(){
        // given
        UserCreateRequest request = new UserCreateRequest(
                "test@test.com",
                "Testman12!@",
                "testman",
                "testnickname",
                UserRole.USER);

        //when
        userService.createUser(request);

        //then
        Optional<User> savedUser = userRepository.findByEmail("test@test.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("test@test.com");
        assertThat(savedUser.get().getName()).isEqualTo("testman");
        assertThat(savedUser.get().getNickname()).isEqualTo("testnickname");
        assertThat(savedUser.get().getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void 중복_이메일_예외처리() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "test@email.com",
                "emailtest12!@",
                "emailtestman",
                "emailtestnickname",
                UserRole.USER);

        //when, then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("중복된 이메일입니다.");

    }
}

