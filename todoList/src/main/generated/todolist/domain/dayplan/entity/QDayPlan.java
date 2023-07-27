package todolist.domain.dayplan.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDayPlan is a Querydsl query type for DayPlan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDayPlan extends EntityPathBase<DayPlan> {

    private static final long serialVersionUID = -202059805L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDayPlan dayPlan = new QDayPlan("dayPlan");

    public final todolist.global.entity.QPlanEntity _super;

    //inherited
    public final StringPath content;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDone;

    // inherited
    public final todolist.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate;

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final todolist.domain.todo.entity.QTodo todo;

    public QDayPlan(String variable) {
        this(DayPlan.class, forVariable(variable), INITS);
    }

    public QDayPlan(Path<? extends DayPlan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDayPlan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDayPlan(PathMetadata metadata, PathInits inits) {
        this(DayPlan.class, metadata, inits);
    }

    public QDayPlan(Class<? extends DayPlan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new todolist.global.entity.QPlanEntity(type, metadata, inits);
        this.content = _super.content;
        this.createdDate = _super.createdDate;
        this.isDone = _super.isDone;
        this.member = _super.member;
        this.modifiedDate = _super.modifiedDate;
        this.todo = inits.isInitialized("todo") ? new todolist.domain.todo.entity.QTodo(forProperty("todo"), inits.get("todo")) : null;
    }

}

