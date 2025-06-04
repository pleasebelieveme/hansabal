package org.example.hansabal.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDib is a Querydsl query type for Dib
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDib extends EntityPathBase<Dib> {

    private static final long serialVersionUID = -1290771179L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDib dib = new QDib("dib");

    public final EnumPath<DibType> dibType = createEnum("dibType", DibType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final org.example.hansabal.domain.users.entity.QUser user;

    public QDib(String variable) {
        this(Dib.class, forVariable(variable), INITS);
    }

    public QDib(Path<? extends Dib> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDib(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDib(PathMetadata metadata, PathInits inits) {
        this(Dib.class, metadata, inits);
    }

    public QDib(Class<? extends Dib> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new org.example.hansabal.domain.users.entity.QUser(forProperty("user")) : null;
    }

}

