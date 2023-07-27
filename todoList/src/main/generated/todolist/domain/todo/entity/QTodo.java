package todolist.domain.todo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodo is a Querydsl query type for Todo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodo extends EntityPathBase<Todo> {

    private static final long serialVersionUID = 1329628105L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodo todo = new QTodo("todo");

    public final todolist.global.entity.QPlanEntity _super;

    //inherited
    public final StringPath content;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate;

    public final ListPath<todolist.domain.dayplan.entity.DayPlan, todolist.domain.dayplan.entity.QDayPlan> dayPlans = this.<todolist.domain.dayplan.entity.DayPlan, todolist.domain.dayplan.entity.QDayPlan>createList("dayPlans", todolist.domain.dayplan.entity.DayPlan.class, todolist.domain.dayplan.entity.QDayPlan.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> deadLine = createDate("deadLine", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> doneDate = createDate("doneDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Importance> importance = createEnum("importance", Importance.class);

    //inherited
    public final BooleanPath isDone;

    // inherited
    public final todolist.domain.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate;

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QTodo(String variable) {
        this(Todo.class, forVariable(variable), INITS);
    }

    public QTodo(Path<? extends Todo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodo(PathMetadata metadata, PathInits inits) {
        this(Todo.class, metadata, inits);
    }

    public QTodo(Class<? extends Todo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new todolist.global.entity.QPlanEntity(type, metadata, inits);
        this.content = _super.content;
        this.createdDate = _super.createdDate;
        this.isDone = _super.isDone;
        this.member = _super.member;
        this.modifiedDate = _super.modifiedDate;
    }

}

