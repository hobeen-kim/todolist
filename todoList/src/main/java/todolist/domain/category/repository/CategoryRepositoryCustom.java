package todolist.domain.category.repository;

import todolist.domain.category.entity.Category;

import java.util.Optional;

public interface CategoryRepositoryCustom {

    Optional<Category> findByIdWithMember(Long categoryId);
}
