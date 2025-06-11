package org.example.hansabal.domain.users.service.integration;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Sql(scripts = {"/user_test_db.sql"}
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

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeAll
    public static void beforeAll() {
        // 테스트 전체 실행 전에 필요한 설정이 있다면 여기에 작성
    }

    @Test
    void 유저_생성_및_암호화_검증(){
        // given
        String rawPassword = "Testman12!@";
        UserCreateRequest request = new UserCreateRequest(
                "test@test.com",
                rawPassword,
                "testman",
                "testnickname",
                UserRole.USER);

        //when
        userService.createUser(request);

        //then
        User savedUser = userRepository.findByEmailOrElseThrow("test@test.com");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(passwordEncoder.matches(rawPassword, savedUser.getPassword())).isTrue();
        assertThat(savedUser.getName()).isEqualTo("testman");
        assertThat(savedUser.getNickname()).isEqualTo("testnickname");
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
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

