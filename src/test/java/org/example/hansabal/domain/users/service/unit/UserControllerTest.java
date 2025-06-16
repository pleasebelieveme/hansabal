//package org.example.hansabal.domain.users.service.unit;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.example.hansabal.common.jwt.JwtFilter;
//import org.example.hansabal.common.jwt.JwtUtil;
//import org.example.hansabal.common.oauth2.CustomOAuth2UserService;
//import org.example.hansabal.common.oauth2.OAuth2LoginSuccessHandler;
//import org.example.hansabal.domain.users.controller.UserController;
//import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
//import org.example.hansabal.domain.users.entity.UserRole;
//import org.example.hansabal.domain.users.repository.RedisRepository;
//import org.example.hansabal.domain.users.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class UserControllerTest {
//
//    @TestConfiguration
//    static class MockServiceConfig {
//        @Bean
//        public UserService userService() {
//            return Mockito.mock(UserService.class);
//        }
//
//        @Bean
//        public JwtUtil jwtUtil() {
//            return Mockito.mock(JwtUtil.class);
//        }
//
//        @Bean
//        public JwtFilter jwtFilter() {
//            return Mockito.mock(JwtFilter.class);
//        }
//
//        @Bean
//        public CustomOAuth2UserService customOAuth2UserService() {
//            return Mockito.mock(CustomOAuth2UserService.class);
//        }
//
//        @Bean
//        public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
//            return Mockito.mock(OAuth2LoginSuccessHandler.class);
//        }
//
//        @Bean
//        public RedisRepository redisRepository() {
//            return Mockito.mock(RedisRepository.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void 사용자_생성_성공() throws Exception {
//        UserCreateRequest request = new UserCreateRequest(
//                "test3@email.com", "!Aa123456", "홍길동", "gildong", UserRole.USER
//        );
//
//        doNothing().when(userService).createUser(Mockito.any(UserCreateRequest.class));
//
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isCreated());
//    }
//}
