package todolist.domain.todo.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import todolist.domain.dayplan.dto.apidto.request.DayPlanCreateApiDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class TodoCreateApiDto {

    @NotBlank(message = "{validation.content}")
    @Size(min = 1, max = 100, message = "{validation.size}")
    private String content;
    @NotNull(message = "{validation.importance}")
    private Importance importance;
    @NotNull(message = "{validation.date}")
    private LocalDate startDate;
    @NotNull(message = "{validation.date}")
    private LocalDate deadLine;

    public TodoCreateServiceDto toServiceDto() {

        return TodoCreateServiceDto.builder()
                .content(this.content)
                .importance(this.importance)
                .startDate(this.startDate)
                .deadLine(this.deadLine)
                .build();

    }

}
