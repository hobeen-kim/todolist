package todolist.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -768386895L;

    public static final QMember member = new QMember("member1");

    public final todolist.global.entity.QBaseEntity _super = new todolist.global.entity.QBaseEntity(this);

    public final EnumPath<Authority> authority = createEnum("authority", Authority.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final ListPath<todolist.domain.dayplan.entity.DayPlan, todolist.domain.dayplan.entity.QDayPlan> dayPlans = this.<todolist.domain.dayplan.entity.DayPlan, todolist.domain.dayplan.entity.QDayPlan>createList("dayPlans", todolist.domain.dayplan.entity.DayPlan.class, todolist.domain.dayplan.entity.QDayPlan.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final ListPath<todolist.domain.todo.entity.Todo, todolist.domain.todo.entity.QTodo> todos = this.<todolist.domain.todo.entity.Todo, todolist.domain.todo.entity.QTodo>createList("todos", todolist.domain.todo.entity.Todo.class, todolist.domain.todo.entity.QTodo.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

