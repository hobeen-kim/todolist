package todolist.domain.toplist.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.annotations.BatchSize;
import todolist.domain.toplist.entity.TopList;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static todolist.domain.category.entity.QCategory.category;
import static todolist.domain.toplist.entity.QTopList.topList;

public class TopListRepositoryImpl implements TopListRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public TopListRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<TopList> findByIdWithMember(Long topListId) {
        return Optional.ofNullable(
                    queryFactory
                        .selectFrom(topList)
                        .join(topList.member).fetchJoin()
                        .where(topList.id.eq(topListId))
                        .fetchOne());
    }

    /**
     * TopList 검색 조건에 따른 조회
     * @param memberId 회원 id
     * @param cond 검색 조건
     * @return 검색 조건에 따른 TopList 목록
     */
    //일단 페이징하지 않는다.
    @Override
    public List<TopList> findByCond(Long memberId, TopListSearchCond cond) {

        return queryFactory
                .selectFrom(topList)
                .join(topList.member).fetchJoin()
                .join(topList.category).fetchJoin()
                .leftJoin(topList.todos).fetchJoin()
                .where(topList.member.id.eq(memberId))
                .where(topList.category.id.eq(cond.getCategoryId()))
                .where(getCond(cond.getFrom(), cond.getTo(), cond.isDone()))
                .fetch();
    }

    private BooleanExpression getCond(LocalDate from, LocalDate to, boolean isDone) {

        BooleanExpression exp = isDone(false);

        if(!isDone){
            return exp;
        }

        if(from == null && to == null){
            return exp.or(isDone(true));
        }

        return exp.or(getBetween(from, to));
    }

    private BooleanExpression isDone(boolean isDone){
        return topList.isDone.eq(isDone);
    }

    private BooleanExpression getBetween(LocalDate from, LocalDate to) {

        if(from == null && to == null){
            return null;
        }
        return topList.doneDate.between(from, to);
    }
}
