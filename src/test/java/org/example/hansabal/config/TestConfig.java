package org.example.hansabal.config;

import org.example.hansabal.common.jwt.JwtFilter;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.users.entity.UserRole;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtFilter jwtFilter() {
        return Mockito.mock(JwtFilter.class);
    }

    // JpaAuditing
    @Bean
    public JpaMetamodelMappingContext jpaMappingContext() {
        return Mockito.mock(JpaMetamodelMappingContext.class);
    }

}
