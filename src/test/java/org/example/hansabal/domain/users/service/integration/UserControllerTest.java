package org.example.hansabal.domain.users.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.dto.request.UserUpdateRequest;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(
                User.builder()
                        .email("user@email.com")
                        .password(passwordEncoder.encode("OldPassword12!@"))
                        .name("테스트유저")
                        .nickname("testnick")
                        .userRole(UserRole.USER)
                        .build()
        );

        // UserAuth를 실제 ID 기반으로 설정
        UserAuth testUserAuth = new UserAuth(testUser.getId(), testUser.getUserRole());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testUserAuth,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + testUserAuth.getUserRole()))
                )
        );
    }

    @Test
    void 사용자_생성_성공() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "new@email.com", "!Aa123456", "newname", "newnick", UserRole.USER
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void 사용자_조회_성공() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_수정_성공() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "updatedNick", "OldPassword12!@", "NewPassword12!@"
        );

        mockMvc.perform(patch("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_삭제_성공() throws Exception {
        mockMvc.perform(delete("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}