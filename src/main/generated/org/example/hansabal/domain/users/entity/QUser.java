package org.example.hansabal.domain.users.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 170959708L;

    public static final QUser user = new QUser("user");

    public final org.example.hansabal.common.base.QBaseEntity _super = new org.example.hansabal.common.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final ListPath<org.example.hansabal.domain.comment.entity.Dib, org.example.hansabal.domain.comment.entity.QDib> dibs = this.<org.example.hansabal.domain.comment.entity.Dib, org.example.hansabal.domain.comment.entity.QDib>createList("dibs", org.example.hansabal.domain.comment.entity.Dib.class, org.example.hansabal.domain.comment.entity.QDib.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<UserRole> userRole = createEnum("userRole", UserRole.class);

    public final EnumPath<UserStatus> userStatus = createEnum("userStatus", UserStatus.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

