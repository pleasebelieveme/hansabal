package org.example.hansabal.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

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
    }

    @Test
    void 로그인_성공() throws Exception {
        LoginRequest request = new LoginRequest(
                "user@email.com","OldPassword12!@"
        );

        mockMvc.perform(post("/api/auth/login")
                .with(user("1").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 로그아웃_성공() throws Exception {
        LoginRequest request = new LoginRequest(
                "user@email.com","OldPassword12!@"
        );
        // 로그인 후 쿠키 또는 토큰 없이도 mockMvc 테스트에서 직접 호출 가능 (단순히 status 확인)
        mockMvc.perform(post("/api/auth/logout")
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 토큰_재발급_성공() throws Exception {
        // given: 실제 로그인 요청
        LoginRequest request = new LoginRequest("user@email.com", "OldPassword12!@");

        String responseBody = mockMvc.perform(post("/api/auth/login")
                .with(user("1").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);

        // when & then: 재발급 요청
        mockMvc.perform(post("/api/auth/reissue")
                .header("Authorization", "Bearer " + tokenResponse.getRefreshToken()))
                .andExpect(status().isOk());
    }
}
