package org.example.hansabal.domain.trade.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRequests is a Querydsl query type for Requests
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRequests extends EntityPathBase<Requests> {

    private static final long serialVersionUID = 346559025L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRequests requests = new QRequests("requests");

    public final org.example.hansabal.common.base.QBaseEntity _super = new org.example.hansabal.common.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final org.example.hansabal.domain.users.entity.QUser requester;

    public final EnumPath<RequestStatus> status = createEnum("status", RequestStatus.class);

    public final QTrade trade;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRequests(String variable) {
        this(Requests.class, forVariable(variable), INITS);
    }

    public QRequests(Path<? extends Requests> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRequests(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRequests(PathMetadata metadata, PathInits inits) {
        this(Requests.class, metadata, inits);
    }

    public QRequests(Class<? extends Requests> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.requester = inits.isInitialized("requester") ? new org.example.hansabal.domain.users.entity.QUser(forProperty("requester")) : null;
        this.trade = inits.isInitialized("trade") ? new QTrade(forProperty("trade"), inits.get("trade")) : null;
    }

}

