package todolist.domain.dayplan.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class DayPlanResponseApiDto {

    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String content;

    private boolean isDone;

    private Long todoId;

    public static DayPlanResponseApiDto of(DayPlanResponseServiceDto dto){

        return new DayPlanResponseApiDto(
                dto.getId(),
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getContent(),
                dto.isDone(),
                dto.getTodoId());
    }
}
