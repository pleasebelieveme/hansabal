package org.example.hansabal.domain.users.service.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.config.TestConfig;
import org.example.hansabal.domain.users.controller.UserController;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequest;
import org.example.hansabal.domain.users.dto.response.UserResponse;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestConfig.class)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 사용자_생성_성공() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "test3@email.com", "!Aa123456", "홍길동", "gildong", UserRole.USER
        );

        doNothing().when(userService).createUser(any(UserCreateRequest.class));

        mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void 사용자_조회_성공() throws Exception {
        UserResponse response = new UserResponse(1L, "test@email.com", "testname", "testnickname");

        when(userService.findById(any())).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.name").value("testname"))
                .andExpect(jsonPath("$.nickname").value("testnickname"));
    }

    @Test
    @WithMockUser
    void 사용자_수정_성공() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("홍길순", "Oldpassword12!@", "Newpassword12!@");

        doNothing().when(userService).updateUser(any(UserUpdateRequest.class), any(UserAuth.class));

        mockMvc.perform(patch("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 사용자_삭제_성공() throws Exception {
        doNothing().when(userService).deleteUser(any(UserAuth.class));

        mockMvc.perform(delete("/api/users"))
                .andExpect(status().isOk());
    }
}