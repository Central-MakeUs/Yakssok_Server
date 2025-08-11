package server.yakssok.domain.user.domain.entity;

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

    private static final long serialVersionUID = -2035483521L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final server.yakssok.domain.QBaseEntity _super = new server.yakssok.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInviteCode inviteCode;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isInitialized = createBoolean("isInitialized");

    public final StringPath nickName = createString("nickName");

    public final StringPath oAuthRefreshToken = createString("oAuthRefreshToken");

    public final EnumPath<OAuthType> oAuthType = createEnum("oAuthType", OAuthType.class);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath providerId = createString("providerId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.inviteCode = inits.isInitialized("inviteCode") ? new QInviteCode(forProperty("inviteCode")) : null;
    }

}

