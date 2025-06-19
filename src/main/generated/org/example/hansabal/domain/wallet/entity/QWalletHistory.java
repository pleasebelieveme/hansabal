package org.example.hansabal.domain.wallet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalletHistory is a Querydsl query type for WalletHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalletHistory extends EntityPathBase<WalletHistory> {

    private static final long serialVersionUID = 1220893297L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalletHistory walletHistory = new QWalletHistory("walletHistory");

    public final org.example.hansabal.common.base.QBaseEntity _super = new org.example.hansabal.common.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Payment> payment = createNumber("payment", Payment.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Long> remain = createNumber("remain", Long.class);

    public final NumberPath<Long> tradeId = createNumber("tradeId", Long.class);

    public final StringPath type = createString("type");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath uuid = createString("uuid");

    public final QWallet wallet;

    public QWalletHistory(String variable) {
        this(WalletHistory.class, forVariable(variable), INITS);
    }

    public QWalletHistory(Path<? extends WalletHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalletHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalletHistory(PathMetadata metadata, PathInits inits) {
        this(WalletHistory.class, metadata, inits);
    }

    public QWalletHistory(Class<? extends WalletHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.wallet = inits.isInitialized("wallet") ? new QWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

