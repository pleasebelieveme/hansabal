package org.example.hansabal.domain.board.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.example.hansabal.common.jwt.JwtFilter;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.config.JsonConfig;
import org.example.hansabal.config.TestConfig;
import org.example.hansabal.domain.board.dto.request.BoardRequest;
import org.example.hansabal.domain.board.dto.response.BoardPageResult;
import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.service.BoardService;
import org.example.hansabal.domain.board.service.BoardServiceUtill;
import org.example.hansabal.domain.users.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;


@ActiveProfiles("test")
@WebMvcTest(controllers = BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JsonConfig.class)
public class BoardControllerTest {


   @MockitoBean
   JwtFilter jwtFilter;

   @MockitoBean
   JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BoardService boardService;

    @MockitoBean
    BoardServiceUtill boardServiceUtill;

    @WithMockUser(username = "1", roles = "USER")
    @Test
    @DisplayName("게시글 등록 성공")
    void createBoardTest() throws Exception {
        // given

        BoardRequest request = new BoardRequest(BoardCategory.DAILY, "제목1", "내용2");
        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .userId(1L)
                .nickname("작성자")
                .email("test@email.com")
                .category(BoardCategory.DAILY)
                .title("제목1")
                .content("내용2")
                .viewCount(0)
                .dibCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .comments(List.of())
                .likeCount(0)
                .likedByMe(false)
                .build();


        Mockito.when(boardService.createBoard(any(), any())).thenReturn(response);


        // when + then
        mockMvc.perform(post("/api/posts")
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nickname").value("작성자"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.category").value("DAILY"))
                .andExpect(jsonPath("$.title").value("제목1"))
                .andExpect(jsonPath("$.content").value("내용2"))
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.dibCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.likedByMe").value(false))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }


    @Test
    @DisplayName("게시글 등록 실패 - Validation 예외")
    void createBoardValidationException() throws Exception {
        BoardRequest invalidRequest = new BoardRequest(BoardCategory.DAILY," ", "내용" );

        mockMvc.perform(post("/api/posts")
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updateBoardTest() throws Exception {
        BoardRequest request = new BoardRequest(BoardCategory.DAILY,"test제목", "test내용" );
        BoardResponse response = BoardResponse.builder()
                .id(2L)
                .userId(1L)
                .nickname("작성자")
                .email("test@email.com")
                .category(BoardCategory.DAILY)
                .title("test제목")
                .content("test내용")
                .viewCount(0)
                .dibCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .comments(List.of())
                .likeCount(0)
                .likedByMe(false)
                .build();


        Mockito.when(boardService.updatePost(any(),eq(1L),any())).thenReturn(response);

        mockMvc.perform(put("/api/posts/1")
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nickname").value("작성자"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.category").value("DAILY"))
                .andExpect(jsonPath("$.title").value("test제목"))
                .andExpect(jsonPath("$.content").value("test내용"))
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.dibCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.likedByMe").value(false))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoardTest() throws Exception {
        mockMvc.perform(delete("/api/posts/1")
                        .with(user("1").roles("USER")))
                .andExpect(status().isOk());

        verify(boardService).deletePost(any(), eq(1L));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    @DisplayName("게시글 단건 조회")
    void getBoardTest() throws Exception {
        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .userId(1L)
                .nickname("작성자")
                .email("test@email.com")
                .category(BoardCategory.DAILY)
                .title("제목")
                .content("내용")
                .viewCount(0)
                .dibCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .comments(List.of())
                .likeCount(0)
                .likedByMe(false)
                .build();

        doNothing().when(boardServiceUtill).viewCount(1L);
        Mockito.when(boardService.getPost(eq(1L))).thenReturn(response);
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nickname").value("작성자"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.category").value("DAILY"))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.dibCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.likedByMe").value(false))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments").isEmpty());
    }

    @Test
    @DisplayName("게시글 목록 조회")
    void getBoardListTest() throws Exception {
        List<BoardSimpleResponse> list = List.of(
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목1", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목2", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목3", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목4", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목5", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목6", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목7", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목8", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목9", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.DAILY, "제목10", 0, 0),
                new BoardSimpleResponse("작성자", BoardCategory.QUESTION, "제목11", 0, 0)

        );
        List<BoardSimpleResponse> pageContent = list.subList(10, 11);

        Page<BoardSimpleResponse> page = new PageImpl<>(pageContent, PageRequest.of(1, 10), 11);

        Mockito.when(boardService.getPosts(eq(BoardCategory.ALL), eq(null), eq(1), eq(10))).thenReturn(BoardPageResult.of(page));

        mockMvc.perform(get("/api/posts")
                        .param("category", "ALL")
                        .param("page", "1")
                        .param("size", "10")
                        .with(user("1").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents.length()").value(1))
                .andExpect(jsonPath("$.contents[0].title").value("제목11"))
                .andExpect(jsonPath("$.contents[0].category").value("QUESTION"));
    }
}
