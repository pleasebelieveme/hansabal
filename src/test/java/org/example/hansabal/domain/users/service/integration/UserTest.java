package org.example.hansabal.domain.users.service.integration;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.response.UserResponse;
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

    // createUser
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

    // findById
    @Test
    void 유저_ID_조회_성공() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "find@test.com",
                "Password12!@",
                "findName",
                "findNickname",
                UserRole.USER
        );
        userService.createUser(request);

        // when
        User savedUser = userRepository.findByEmailOrElseThrow("find@test.com");
        UserAuth userAuth = new UserAuth(savedUser.getId(), savedUser.getUserRole());
        UserResponse response = UserResponse.from(savedUser);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(savedUser.getId());
        assertThat(response.email()).isEqualTo("find@test.com");
        assertThat(response.name()).isEqualTo("findName");
        assertThat(response.nickname()).isEqualTo("findNickname");
    }

    @Test
    void 유저_ID_조회_실패_예외발생() {
        // given
        Long id = 9999L;
        UserAuth userAuth = new UserAuth(id, UserRole.USER);

        // when & then
        assertThatThrownBy(() -> userService.findById(userAuth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

}

