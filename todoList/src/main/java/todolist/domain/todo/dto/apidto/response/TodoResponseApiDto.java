package todolist.domain.todo.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todolist.domain.dayplan.dto.apidto.response.DayPlanResponseApiDto;
import todolist.domain.dayplan.dto.servicedto.DayPlanResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class TodoResponseApiDto {

    private Long id;
    private Importance importance;
    private String content;
    private boolean isDone;
    private LocalDate startDate;
    private LocalDate deadLine;
    private LocalDate doneDate;

    public static TodoResponseApiDto of(TodoResponseServiceDto dto){

        return new TodoResponseApiDto(
                dto.getId(),
                dto.getImportance(),
                dto.getContent(),
                dto.isDone(),
                dto.getStartDate(),
                dto.getDeadLine(),
                dto.getDoneDate());
    }
}
