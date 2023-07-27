package todolist.domain.todo.dto.apidto.request;

import jakarta.validation.constraints.NotBlank;
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
    private String content;
    @NotBlank(message = "{validation.importance}")
    private Importance importance;
    @NotBlank(message = "{validation.date}")
    private LocalDate startDate;
    @NotBlank(message = "{validation.date}")
    private LocalDate deadLine;

    public static TodoCreateServiceDto toServiceDto(TodoCreateApiDto dto) {

        return TodoCreateServiceDto.builder()
                .content(dto.getContent())
                .importance(dto.getImportance())
                .startDate(dto.getStartDate())
                .deadLine(dto.getDeadLine())
                .build();

    }

}
