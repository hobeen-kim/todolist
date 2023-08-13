package todolist.domain.todo.repository;

import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.searchCond.DateTypeSearchCond;

import java.util.List;
import java.util.Optional;


public interface TodoRepositoryCustom {

    Optional<Todo> findByIdWithMember(Long id);

    List<Todo> findByCond(Long memberId, DateTypeSearchCond cond);

    List<Todo> findByCond(Long memberId, Long categoryId, DateTypeSearchCond cond);
}
