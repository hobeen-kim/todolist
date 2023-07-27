package todolist.domain.dayplan.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanUpdateServiceDto;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class DayPlanUpdateApiDto {

    private String content;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isDone;
    private Long todoId;
    //todo 와 연관관계를 끊으려면 false 를 주면 된다.
    private Boolean deleteTodo;

    public DayPlanUpdateServiceDto toServiceDto() {

        return DayPlanUpdateServiceDto.builder()
                .content(this.content)
                .date(this.date)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .isDone(this.isDone)
                .todoId(this.todoId)
                .deleteTodo(this.deleteTodo)
                .build();

    }

}
