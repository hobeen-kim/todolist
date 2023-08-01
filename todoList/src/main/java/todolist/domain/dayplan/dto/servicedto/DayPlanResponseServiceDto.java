package todolist.domain.dayplan.dto.servicedto;

import lombok.*;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DayPlanResponseServiceDto {

    private Long id;
    private String content;
    private boolean isDone;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long todoId;
    private Long categoryId;

    public static DayPlanResponseServiceDto of(DayPlan dayPlan) {
        return new DayPlanResponseServiceDto(
                dayPlan.getId(),
                dayPlan.getContent(),
                dayPlan.isDone(),
                dayPlan.getDate(),
                dayPlan.getStartTime(),
                dayPlan.getEndTime(),
                Optional.ofNullable(dayPlan.getTodo()).map(Todo::getId).orElse(null),
                Optional.ofNullable(dayPlan.getCategory()).map(Category::getId).orElse(null)
        );
    }

    public static List<DayPlanResponseServiceDto> of(List<DayPlan> dayPlans) {

        List<DayPlanResponseServiceDto> dayPlanResponseServiceDtos = new ArrayList<>();
        for(DayPlan dayPlan : dayPlans){
            dayPlanResponseServiceDtos.add(DayPlanResponseServiceDto.of(dayPlan));
        }
        return dayPlanResponseServiceDtos;
    }
}
