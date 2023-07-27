package todolist.domain.todo.dto.apidto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.global.reponse.PageInfo;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class TodoListResponseApiDto {

    private List<TodoResponseApiDto> todos;
    private PageInfo pageInfo;

    public static TodoListResponseApiDto of(Page<TodoResponseServiceDto> page){
        return null;
    }

    public static TodoListResponseApiDto of(List<TodoResponseServiceDto> todos){
        List<TodoResponseApiDto> dtos = new ArrayList<>();
        for(TodoResponseServiceDto todo : todos){
            dtos.add(TodoResponseApiDto.of(todo));
        }

        return new TodoListResponseApiDto(dtos, null);
    }
}
