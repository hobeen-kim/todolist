package todolist.domain.dayplan.repository;

import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.dayplan.repository.searchCond.DateSearchCond;

import java.util.List;
import java.util.Optional;

public interface DayPlanRepositoryCustom {

    List<DayPlan> findByCond(Long memberId, DateSearchCond cond);

    Optional<DayPlan> findByIdWithMember(Long id);
}
