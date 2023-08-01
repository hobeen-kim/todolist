package todolist.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import todolist.domain.category.entity.Category;
import todolist.domain.category.entity.QCategory;

import java.util.Optional;

import static todolist.domain.category.entity.QCategory.*;
import static todolist.domain.dayplan.entity.QDayPlan.dayPlan;

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
}
