package todolist.domain.category.repository;

import todolist.domain.category.entity.Category;

import java.time.LocalDate;
import java.util.Optional;

public interface CategoryRepositoryCustom {

    Optional<Category> findByIdWithMember(Long categoryId);

    Optional<Category> findAllInfoById(Long memberId, Long categoryId, LocalDate today);
}
