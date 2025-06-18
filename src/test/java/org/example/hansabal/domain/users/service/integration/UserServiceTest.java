package org.example.hansabal.domain.users.service.integration;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequest;
import org.example.hansabal.domain.users.dto.response.UserResponse;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"/user_test_db.sql"}
        ,executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
public class UserServiceTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

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
        UserAuth userAuth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());
        UserResponse response = userService.findById(userAuth);

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
        UserAuth userAuth = new UserAuth(id, UserRole.USER, "testnickname");

        // when & then
        assertThatThrownBy(() -> userService.findById(userAuth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }

    // updateUser
    @Test
    void 비밀번호_불일치_예외발생() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "wrongpass@test.com",
                "OriginalPassword12!@",
                "WrongPassName",
                "WrongPassNick",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("wrongpass@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        UserUpdateRequest request = new UserUpdateRequest(
                "newNickname",
                "wrongPassword12!@", // 잘못된 oldPassword
                "newPassword12!@"
        );

        // when & then
        assertThatThrownBy(() -> userService.updateUser(request, auth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("유효하지 않은 비밀번호입니다.");
    }

    @Test
    void 변경값_없을때_예외발생() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "wrongpass@test.com",
                "OriginalPassword12!@",
                "WrongPassName",
                "WrongPassNick",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("wrongpass@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                "OriginalPassword12!@",
                null
        );

        // when & then
        assertThatThrownBy(() -> userService.updateUser(request, auth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("닉네임이나 새 비밀번호 중 하나는 반드시 입력되어야 합니다.");
    }

    @Test
    void 동일한_닉네임_변경요청_예외발생() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "sameName@test.com",
                "OriginalPassword12!@",
                "sameName",
                "sameNick",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("sameName@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());
        UserUpdateRequest request = new UserUpdateRequest(
                savedUser.getNickname(),
                "OriginalPassword12!@",
                null
        );

        // when & then
        assertThatThrownBy(() -> userService.updateUser(request, auth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("현재 계정의 닉네임과 같습니다.");
    }

    @Test
    void 동일한_비밀번호_변경요청_예외발생() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "samePass@test.com",
                "OriginalPassword12!@",
                "samePass",
                "sameNick",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("samePass@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                "OriginalPassword12!@",
                "OriginalPassword12!@"
        );

        // when & then
        assertThatThrownBy(() -> userService.updateUser(request, auth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("현재 계정의 비밀번호와 같습니다.");
    }

    @Test
    void 닉네임만_변경_성공() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "onlyNick@test.com",
                "OriginalPassword12!@",
                "name",
                "nickname",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("onlyNick@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        UserUpdateRequest request = new UserUpdateRequest(
                "newNickname",
                "OriginalPassword12!@",
                null
        );

        // when
        userService.updateUser(request, auth);

        // then
        User updatedUser = userRepository.findByIdOrElseThrow(savedUser.getId());
        assertThat(updatedUser.getNickname()).isEqualTo("newNickname");
    }

    @Test
    void 비밀번호만_변경_성공() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "onlyPass@test.com",
                "OldPassword12!@",
                "name",
                "nickname",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("onlyPass@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        String newPassword = "Newpass12!@";

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                "OldPassword12!@",
                newPassword
        );

        // when
        userService.updateUser(request, auth);

        // then
        User updated = userRepository.findByIdOrElseThrow(savedUser.getId());
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    void 닉네임_비밀번호_모두_변경_성공() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "change@test.com",
                "OldPassword12!@",
                "name",
                "nickname",
                UserRole.USER
        );
        userService.createUser(create);

        User savedUser = userRepository.findByEmailOrElseThrow("change@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        String newPassword = "Changed12!@";

        UserUpdateRequest request = new UserUpdateRequest(
                "changeNickname",
                "OldPassword12!@",
                newPassword
        );

        // when
        userService.updateUser(request, auth);

        // then
        User updated = userRepository.findByIdOrElseThrow(savedUser.getId());
        assertThat(updated.getNickname()).isEqualTo("changeNickname");
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    void 회원_삭제_성공() {
        // given
        UserCreateRequest create = new UserCreateRequest(
                "delete@test.com",
                "Password12!@",
                "name",
                "nickname",
                UserRole.USER
        );
        userService.createUser(create);
        User savedUser = userRepository.findByEmailOrElseThrow("delete@test.com");
        UserAuth auth = new UserAuth(savedUser.getId(), savedUser.getUserRole(), savedUser.getNickname());

        // when
        userService.deleteUser(auth);

        // then
        assertThatThrownBy(() -> userRepository.findByIdOrElseThrow(savedUser.getId()))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다."); // 예외 메시지는 상황에 맞게 수정
    }

    @Test
    void 존재하지_않는_유저_삭제시_예외발생() {
        // given
        UserAuth auth = new UserAuth(9999L, UserRole.USER, "emptynickname"); // 존재하지 않는 ID

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(auth))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다.");
    }
}

