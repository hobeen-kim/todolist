package todolist.domain.dayplan.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.dayplan.repository.searchCond.DateSearchCond;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static todolist.domain.dayplan.entity.QDayPlan.dayPlan;

public class DayPlanRepositoryImpl implements DayPlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DayPlanRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<DayPlan> findByIdWithMember(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dayPlan)
                        .join(dayPlan.member).fetchJoin()
                        .where(dayPlan.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public List<DayPlan> findByCond(Long memberId, DateSearchCond cond) {
        return queryFactory
                .selectFrom(dayPlan)
                .join(dayPlan.member).fetchJoin()
                .leftJoin(dayPlan.todo).fetchJoin()
                .where(dayPlan.member.id.eq(memberId))
                .where(getBetween(cond.getFrom(), cond.getTo()))
                .fetch();
    }

    private BooleanExpression getBetween(LocalDate from, LocalDate to) {

        if(from == null && to == null){
            return null;
        }
        return dayPlan.date.between(from, to);
    }
}
