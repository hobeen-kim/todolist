package todolist.domain.dayplan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.category.entity.Category;
import todolist.domain.category.service.CategoryService;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanUpdateServiceDto;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.dayplan.repository.DayPlanRepository;
import todolist.domain.dayplan.repository.searchCond.DateSearchCond;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.service.TodoService;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;
import todolist.global.exception.buinessexception.planexception.PlanNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DayPlanService {

    private final DayPlanRepository dayPlanRepository;
    private final CustomUserDetailsService userDetailsService;
    private final CategoryService categoryService;
    private final TodoService todoService;

    /**
     * DayPlan 을 생성하고 저장합니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param dto DayPlan 생성에 필요한 정보
     * @return 생성된 DayPlan
     */
    @Transactional
    public DayPlanResponseServiceDto saveDayPlan(Long memberId, DayPlanCreateServiceDto dto) {

        Member verifiedMember = verifiedMember(memberId);
        Category verifiedCategory = categoryService.verifiedCategory(memberId, dto.getCategoryId());

        DayPlan dayPlan = DayPlan.createDayPlan(
                verifiedMember,
                verifiedCategory,
                dto.getContent(),
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if(dto.getTodoId() != null){
            addDayPlanToTodo(memberId, dto.getTodoId(), dayPlan);
        }

        dayPlanRepository.save(dayPlan);

        return DayPlanResponseServiceDto.of(dayPlan);
    }

    public List<DayPlanResponseServiceDto> findDayPlanList(Long memberId, LocalDate from, LocalDate to) {
        DateSearchCond cond = new DateSearchCond(from, to);

        List<DayPlan> dayPlans = dayPlanRepository.findByCond(memberId, cond);

        return DayPlanResponseServiceDto.of(dayPlans);
    }

    @Transactional
    //Todo : dayPlanId 도 dto 로 받는걸로 통일하자
    public void updateDayPlan(Long memberId, Long dayPlanId, DayPlanUpdateServiceDto dto) {
        DayPlan dayPlan = verifiedDayPlan(memberId, dayPlanId);

        update(dayPlan, dto);
    }

    @Transactional
    public void deleteDayPlan(Long memberId, Long dayPlanId) {

        DayPlan dayPlan = verifiedDayPlan(memberId, dayPlanId);
        dayPlanRepository.delete(dayPlan);
    }

    private DayPlan verifiedDayPlan(Long memberId, Long dayPlanId) {
        DayPlan dayPlan = dayPlanRepository.findByIdWithMember(dayPlanId)
                .orElseThrow(PlanNotFoundException::new);

        if(!dayPlan.getMember().getId().equals(memberId)){
            throw new PlanAccessDeniedException();
        }

        return dayPlan;
    }

    private void addDayPlanToTodo(Long memberId, Long todoId, DayPlan dayPlan) {
        Todo todo = todoService.verifiedTodo(memberId, todoId);
        dayPlan.addTodo(todo);
    }

    private void addDayPlanToCategory(Long memberId, Long categoryId, DayPlan dayPlan) {
        Category category = categoryService.verifiedCategory(memberId, categoryId);
        dayPlan.changeCategory(category);
    }

    private void addDayPlanToMember(Long memberId, DayPlan dayPlan) {
        Member member = verifiedMember(memberId);
        member.addDayPlans(dayPlan);
    }

    private void update(DayPlan dayPlan, DayPlanUpdateServiceDto dto) {
        Optional.ofNullable(dto.getContent())
                .ifPresent(dayPlan::changeContent);
        Optional.ofNullable(dto.getDate())
                .ifPresent(dayPlan::changeDate);
        Optional.ofNullable(dto.getIsDone())
                .ifPresent(dayPlan::isDone);
        Optional.ofNullable(dto.getTodoId())
                .ifPresent(id -> addDayPlanToTodo(
                        dayPlan.getMember().getId(),
                        id,
                        dayPlan
                ));
        Optional.ofNullable(dto.getDeleteTodo())
                        .ifPresent(isDelete -> {
                            if(isDelete){
                                dayPlan.removeTodo();
                            }
                        });
        dayPlan.changeTime(dto.getStartTime(), dto.getEndTime());
    }

    private Member verifiedMember(Long memberId){
        return userDetailsService.loadUserById(memberId);
    }
}
