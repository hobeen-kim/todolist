package todolist.domain.dayplan.dto.servicedto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DayPlanCreateServiceDto {

    private String content;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long todoId;
}
