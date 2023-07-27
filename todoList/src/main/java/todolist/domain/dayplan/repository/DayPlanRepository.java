package todolist.domain.dayplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import todolist.domain.dayplan.entity.DayPlan;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long>, DayPlanRepositoryCustom{
}