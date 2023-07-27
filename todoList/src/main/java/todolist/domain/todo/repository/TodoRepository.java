package todolist.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todolist.domain.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {
}