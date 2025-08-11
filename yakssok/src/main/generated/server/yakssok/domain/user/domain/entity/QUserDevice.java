package server.yakssok.domain.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserDevice is a Querydsl query type for UserDevice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserDevice extends EntityPathBase<UserDevice> {

    private static final long serialVersionUID = 805792693L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserDevice userDevice = new QUserDevice("userDevice");

    public final BooleanPath alertOn = createBoolean("alertOn");

    public final StringPath deviceId = createString("deviceId");

    public final StringPath fcmToken = createString("fcmToken");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser user;

    public QUserDevice(String variable) {
        this(UserDevice.class, forVariable(variable), INITS);
    }

    public QUserDevice(Path<? extends UserDevice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserDevice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserDevice(PathMetadata metadata, PathInits inits) {
        this(UserDevice.class, metadata, inits);
    }

    public QUserDevice(Class<? extends UserDevice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

