package org.example.hansabal.domain.board;


import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.repository.BoardRepository;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.comment.repository.CommentRepository;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
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
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;

    @Test
    @DisplayName("게시글 등록 성공")
    void 게시글_등록() {
        BoardRequest request = new BoardRequest("일상", "제목", "내용");
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);

        BoardResponse response = boardService.createPost(userAuth, request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContent()).isEqualTo("내용");
        assertThat(response.getCategory()).isEqualTo("일상");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정() {
        BoardRequest request = new BoardRequest("DAILY", "수정 제목", "수정 내용");
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);

        BoardResponse response = boardService.updatePost(userAuth, 1L, request);

        assertThat(response.getTitle()).isEqualTo("수정 제목");
        assertThat(response.getContent()).isEqualTo("수정 내용");
        assertThat(response.getCategory()).isEqualTo("일상");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void 게시글_삭제() {
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);

        boardService.deletePost(userAuth, 1L);

        Optional<Board> deleted = boardRepository.findById(1L);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void 게시글_상세조회() {
        UserAuth userAuth = new UserAuth(1L, UserRole.USER);
        BoardResponse response = boardService.getPost(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("test title");
        assertThat(response.getContent()).isEqualTo("test content");
        assertThat(response.getCategory()).isEqualTo("정보");
    }

    @Test
    @DisplayName("게시글 목록 조회 - 전체 + 키워드")
    void 게시글_목록조회_전체_키워드() {
        Page<BoardResponse> result = boardService.getPosts("ALL", "테스트", 0, 10);

        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 카테고리만")
    void 게시글_목록조회_카테고리() {
        Page<BoardResponse> result = boardService.getPosts("NOTICE", null, 0, 10);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("공지");
    }

    @Test
    @DisplayName("게시글 목록 조회 - 카테고리 + 키워드")
    void 게시글_목록조회_카테고리_키워드() {
        Page<BoardResponse> result = boardService.getPosts("NOTICE", "내용", 0, 10);

        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 수정 - 권한 없음 예외")
    void 게시글_수정_권한없음() {
        BoardRequest request = new BoardRequest("NOTICE", "제목", "내용");
        UserAuth userAuth = new UserAuth(2L, UserRole.USER);

        assertThatThrownBy(() -> boardService.updatePost(userAuth, 1L, request))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("권한");
    }

    @Test
    @DisplayName("게시글 삭제 - 권한 없음 예외")
    void 게시글_삭제_권한없음() {
        UserAuth userAuth = new UserAuth(2L, UserRole.USER);

        assertThatThrownBy(() -> boardService.deletePost(userAuth, 1L))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("권한");
    }
    @Test
    @DisplayName("게시글 상세 조회 - 없는 ID 예외")
    void 게시글_상세조회_예외() {
        assertThatThrownBy(() -> boardService.getPost(9999L))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("존재하지");
    }

}
