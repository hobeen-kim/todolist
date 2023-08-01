package todolist.domain.toplist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todolist.domain.toplist.entity.TopList;

public interface TopListRepository extends JpaRepository<TopList, Long>, TopListRepositoryCustom {
}