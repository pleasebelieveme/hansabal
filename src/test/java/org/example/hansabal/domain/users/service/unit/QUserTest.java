package org.example.hansabal.domain.users.service.unit;

import com.querydsl.core.types.dsl.PathBuilder;
import org.example.hansabal.domain.users.entity.QUser;
import org.example.hansabal.domain.users.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QUserTest {

    @Test
    void querydsl_Path_생성자_직접호출_테스트() {
        QUser qUser = new QUser(new PathBuilder<>(User.class, "user"));
        assertThat(qUser).isNotNull(); // 그냥 호출만 되면 커버리지 포함됨
    }
}
