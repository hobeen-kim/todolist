package todolist.domain.dayplan.dto.servicedto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class DayPlanUpdateServiceDto {

    private String content;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isDone;
    private Long todoId;
    //todo 와 연관관계를 끊으려면 false 를 주면 된다.
    private Boolean deleteTodo;
    private Long categoryId;

}
