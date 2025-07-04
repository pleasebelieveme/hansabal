package org.example.hansabal.domain.board;


import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardPageResult;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardServiceReadTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BoardService boardService;



    @BeforeAll
    @Transactional
    void setup() {
        for (int i = 0; i < 10; i++) {
            String email = "readtest_user" + i + "@example.com";
            String nickname = "nickname" + i;

            UserCreateRequest request = new UserCreateRequest(
                    email,
                    "@Aa123456",
                    "테스트이름",
                    nickname,
                    UserRole.USER
            );

            userService.createUser(request);
        }
        // 유저 조회 후 UserAuth 생성
        UserAuth userAuth = new UserAuth(1L, UserRole.USER, "nickname0");
        BoardRequest boardRequest = new BoardRequest(
                BoardCategory.DAILY,
                "테스트 제목",
                "테스트 내용"
        );
        boardService.createBoard(userAuth, boardRequest);



    }
    @Test
    @DisplayName("게시글 상세 조회 성공")
    void 게시글_상세조회() {
        UserAuth userAuth = new UserAuth(1L, UserRole.USER,"nickname0");
        BoardResponse response = boardService.getPost(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        assertThat(response.getCategory()).isEqualTo(BoardCategory.DAILY);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 전체 + 키워드")
    void 게시글_목록조회_전체_키워드() {
        BoardPageResult result = boardService.getPosts(BoardCategory.ALL, "테스트", 0, 10);

        assertThat(result.getContents()).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 목록 조회 - 카테고리만")
    void 게시글_목록조회_카테고리() {
        BoardPageResult result = boardService.getPosts(BoardCategory.DAILY, null, 0, 10);

        assertThat(result.getContents()).isNotEmpty();
        assertThat(result.getContents().get(0).getCategory()).isEqualTo(BoardCategory.DAILY);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 카테고리 + 키워드")
    void 게시글_목록조회_카테고리_키워드() {
        BoardPageResult result = boardService.getPosts(BoardCategory.DAILY, "테스트 내용", 0, 10);

        assertThat(result.getContents()).isNotEmpty();
    }

    @Test
    @DisplayName("게시글 수정 - 권한 없음 예외")
    void 게시글_수정_권한없음() {
        BoardRequest request = new BoardRequest(BoardCategory.DAILY, "제목", "내용");
        UserAuth userAuth = new UserAuth(2L, UserRole.USER,"nickname0");

        assertThatThrownBy(() -> boardService.updatePost(userAuth, 1L, request))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("권한");
    }

    @Test
    @DisplayName("게시글 삭제 - 권한 없음 예외")
    void 게시글_삭제_권한없음() {
        UserAuth userAuth = new UserAuth(2L, UserRole.USER,"nickname0");

        assertThatThrownBy(() -> boardService.deletePost(userAuth, 1L))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("권한");
    }
    @Test
    @DisplayName("게시글 상세 조회 - 없는 ID 예외")
    void 게시글_상세조회_예외() {
        assertThatThrownBy(() -> boardService.getPost(9999L))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다.");
    }

}
