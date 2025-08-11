package server.yakssok.domain.friend.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriend is a Querydsl query type for Friend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriend extends EntityPathBase<Friend> {

    private static final long serialVersionUID = -781431457L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriend friend = new QFriend("friend");

    public final server.yakssok.domain.user.domain.entity.QUser following;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath relationName = createString("relationName");

    public final server.yakssok.domain.user.domain.entity.QUser user;

    public QFriend(String variable) {
        this(Friend.class, forVariable(variable), INITS);
    }

    public QFriend(Path<? extends Friend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriend(PathMetadata metadata, PathInits inits) {
        this(Friend.class, metadata, inits);
    }

    public QFriend(Class<? extends Friend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.following = inits.isInitialized("following") ? new server.yakssok.domain.user.domain.entity.QUser(forProperty("following"), inits.get("following")) : null;
        this.user = inits.isInitialized("user") ? new server.yakssok.domain.user.domain.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

