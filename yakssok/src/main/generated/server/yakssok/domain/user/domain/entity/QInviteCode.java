package server.yakssok.domain.user.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInviteCode is a Querydsl query type for InviteCode
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QInviteCode extends BeanPath<InviteCode> {

    private static final long serialVersionUID = 854416970L;

    public static final QInviteCode inviteCode = new QInviteCode("inviteCode");

    public final StringPath value = createString("value");

    public QInviteCode(String variable) {
        super(InviteCode.class, forVariable(variable));
    }

    public QInviteCode(Path<? extends InviteCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInviteCode(PathMetadata metadata) {
        super(InviteCode.class, metadata);
    }

}

