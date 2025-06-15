package org.example.hansabal.domain.users.service.unit;

import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserErrorCodeTest {

    @Test
    void 모든_UserErrorCode_enum_값_검증() {
        for (UserErrorCode code : UserErrorCode.values()) {
            assertThat(code.getCode()).isNotBlank();
            assertThat(code.getMessage()).isNotBlank();
            assertThat(code.getStatus()).isInstanceOf(HttpStatus.class);
        }
    }
}