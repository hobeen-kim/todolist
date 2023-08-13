package todolist.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.searchCond.DateTypeSearchCond;
import todolist.domain.todo.repository.searchCond.SearchType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static todolist.domain.todo.entity.QTodo.*;
import static todolist.domain.todo.repository.searchCond.SearchType.*;

public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public TodoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<Todo> findByIdWithMember(Long todoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .join(todo.member).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public List<Todo> findByCond(Long memberId, DateTypeSearchCond cond) {

        return queryFactory
                .selectFrom(todo)
                .join(todo.member).fetchJoin()
                .leftJoin(todo.dayPlans).fetchJoin()
                .where(todo.member.id.eq(memberId))
                .where(getBetween(cond.getFrom(), cond.getTo(), cond.getSearchType()))
                .fetch();
    }

    @Override
    public List<Todo> findByCond(Long memberId, Long categoryId, DateTypeSearchCond cond) {

        return queryFactory
                .selectFrom(todo)
                .join(todo.member).fetchJoin()
                .leftJoin(todo.dayPlans).fetchJoin()
                .where(todo.member.id.eq(memberId))
                .where(todo.category.id.eq(categoryId))
                .where(getBetween(cond.getFrom(), cond.getTo(), cond.getSearchType()))
                .fetch();
    }

    private BooleanExpression getBetween(LocalDate from, LocalDate to, SearchType searchType) {

        if(from == null && to == null){
            return todo.isDone.eq(false);
        }
        if(searchType.equals(START_DATE)){
            return todo.startDate.between(from, to);
        }
        else if(searchType.equals(DEAD_LINE)){
            return todo.deadLine.between(from, to);
        }
        else if(searchType.equals(DONE_DATE)){
            return todo.doneDate.between(from, to);
        }
        else{
            return null;
        }

    }
}
