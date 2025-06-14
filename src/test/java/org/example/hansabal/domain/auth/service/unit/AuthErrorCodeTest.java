package org.example.hansabal.domain.auth.service.unit;

import org.example.hansabal.domain.auth.exception.AuthErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthErrorCodeTest {

    @Test
    void 모든_AuthErrorCode_enum_값_검증() {
        for (AuthErrorCode code : AuthErrorCode.values()) {
            assertThat(code.getCode()).isNotBlank();
            assertThat(code.getMessage()).isNotBlank();
            assertThat(code.getStatus()).isInstanceOf(HttpStatus.class);
        }
    }
}