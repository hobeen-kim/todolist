package todolist.domain.todo.dto.apidto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoUpdateServiceDto;
import todolist.domain.todo.entity.Importance;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class TodoUpdateApiDto {

    private String content;
    private Importance importance;
    private LocalDate startDate;
    private LocalDate deadLine;
    private LocalDate doneDate;

    public TodoUpdateServiceDto toServiceDto(Long todoId) {

        return TodoUpdateServiceDto.builder()
                .id(todoId)
                .content(this.content)
                .importance(this.importance)
                .startDate(this.startDate)
                .deadLine(this.deadLine)
                .doneDate(this.doneDate)
                .build();

    }

}
