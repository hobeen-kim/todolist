package todolist.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todolist.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
}