package todolist.domain.toplist.repository;

import todolist.domain.toplist.entity.TopList;
import todolist.domain.toplist.repository.searchCond.TopListSearchCond;

import java.util.List;
import java.util.Optional;

public interface TopListRepositoryCustom {

    Optional<TopList> findByIdWithMember(Long topListId);

    List<TopList> findByCond(Long memberId, TopListSearchCond cond);
}
