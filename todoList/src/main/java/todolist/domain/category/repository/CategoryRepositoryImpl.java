package todolist.domain.category.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import todolist.domain.category.entity.Category;
import todolist.domain.category.entity.QCategory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static todolist.domain.category.entity.QCategory.*;
import static todolist.domain.dayplan.entity.QDayPlan.dayPlan;
import static todolist.domain.member.entity.QMember.member;
import static todolist.domain.todo.entity.QTodo.todo;
import static todolist.domain.toplist.entity.QTopList.topList;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CategoryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Category> findByIdWithMember(Long categoryId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(category)
                        .join(category.member).fetchJoin()
                        .where(category.id.eq(categoryId))
                        .fetchOne()
        );
    }

    //todo : 해당 날짜부터 4주차까지의 day_plans 조회
    //todo : 모든 연관 top_List 조회
    //todo : DeadLine 이 지나지 않은 미완료 todo 조회
    @Override
    public Optional<Category> findAllInfoById(Long memberId, Long categoryId, LocalDate today) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(category)
                        .join(category.member, member).fetchJoin()
                        .where(category.id.eq(categoryId).and(member.id.eq(memberId)))
                        .fetchOne()
        );
    }



    BooleanExpression dayPlanDateBetween(LocalDate today){

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusWeeks(4).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return dayPlan.date.between(startOfWeek, endOfWeek);
    }

    BooleanExpression topListNotDone(){
        return topList.isDone.isFalse();
    }

    BooleanExpression todoDateBefore(LocalDate today){
        return todo.deadLine.after(today).or(todo.isDone.isFalse());
    }


}
