package org.example.hansabal.domain.board;


import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.comment.service.CommentService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
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
@Slf4j
@Rollback(false)
@Sql(scripts = {"/comment_user_test_db.sql", "/comment_board_test_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BoardServiceTest {

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


    @Autowired private BoardService boardService;
    @Autowired private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 등록 성공")
    void 게시글_등록() {
        BoardRequest request = new BoardRequest(BoardCategory.DAILY, "제목", "내용");
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

        BoardResponse response = boardService.createPost(userAuth, request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
        assertThat(response.getCategory()).isEqualTo(BoardCategory.DAILY);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정() {
        BoardRequest request = new BoardRequest(BoardCategory.DAILY, "수정 제목", "수정 내용");
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

        BoardResponse response = boardService.updatePost(userAuth, 1L, request);

        assertThat(response.getTitle()).isEqualTo("수정 제목");
        assertThat(response.getContent()).isEqualTo("수정 내용");
        assertThat(response.getCategory()).isEqualTo(BoardCategory.DAILY);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void 게시글_삭제() {
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"testnickname1");

        boardService.deletePost(userAuth, 1L);

        Optional<Board> deleted = boardRepository.findById(1L);
        assertThat(deleted).isEmpty();
    }


}
