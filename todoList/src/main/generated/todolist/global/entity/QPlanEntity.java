package todolist.global.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlanEntity is a Querydsl query type for PlanEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QPlanEntity extends EntityPathBase<PlanEntity> {

    private static final long serialVersionUID = -829059402L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlanEntity planEntity = new QPlanEntity("planEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final BooleanPath isDone = createBoolean("isDone");

    public final todolist.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public QPlanEntity(String variable) {
        this(PlanEntity.class, forVariable(variable), INITS);
    }

    public QPlanEntity(Path<? extends PlanEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlanEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlanEntity(PathMetadata metadata, PathInits inits) {
        this(PlanEntity.class, metadata, inits);
    }

    public QPlanEntity(Class<? extends PlanEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new todolist.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

