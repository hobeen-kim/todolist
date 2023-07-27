package todolist.domain.todo.dto.servicedto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.entity.Todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoResponseServiceDto {

    private Long id;
    private Importance importance;
    private String content;
    private boolean isDone;
    private LocalDate startDate;
    private LocalDate deadLine;
    private LocalDate doneDate;

    public static TodoResponseServiceDto of(Todo todo) {
        return new TodoResponseServiceDto(
                todo.getId(),
                todo.getImportance(),
                todo.getContent(),
                todo.isDone(),
                todo.getStartDate(),
                todo.getDeadLine(),
                todo.getDoneDate()
        );
    }

    public static List<TodoResponseServiceDto> of(List<Todo> todos) {

        List<TodoResponseServiceDto> todoResponseServiceDtos = new ArrayList<>();
        for(Todo todo : todos){
            todoResponseServiceDtos.add(TodoResponseServiceDto.of(todo));
        }
        return todoResponseServiceDtos;
    }

}
