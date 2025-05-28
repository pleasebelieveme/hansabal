package org.example.hansabal.domain.auth.dto;

import java.io.Serializable;

import org.example.hansabal.domain.users.entity.User;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserAuth implements Serializable {

    // 자바: 직렬화할 때 serialVersionUID 값을 함께 저장
    // 이후 역직렬화할 때 이 값이 현재 클래스의 serialVersionUID 와 일치해야만 복원이 가능
    // 선언하지 않으면 JVM 이 자동으로 생성하지만, 클래스 변경이 생길 경우 예상치 못한 InvalidClassException 발생
    // --> 클래스 구조가 바뀌어도 serialVersionUID 가 같으면 역직렬화 에러 방지 + 세션 호환성 유지
    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;

    public static UserAuth from(User user) {
        return UserAuth.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}
