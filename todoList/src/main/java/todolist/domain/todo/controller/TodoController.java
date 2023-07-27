package todolist.domain.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.todo.dto.apidto.request.TodoCreateApiDto;
import todolist.domain.todo.dto.apidto.response.TodoListResponseApiDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.domain.todo.service.TodoService;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<ApiResponse<TodoListResponseApiDto>> getTodos(LocalDate from, LocalDate to, SearchType searchType){

        Long memberId = SecurityUtil.getCurrentId();

        List<TodoResponseServiceDto> todoList = todoService.findTodoList(memberId, from, to, searchType);

        ApiResponse<TodoListResponseApiDto> response = buildApiOkResponse(todoList);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity createTodo(){
        return null;
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity updateTodo(){
        return null;
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity deleteTodo(){
        return null;
    }


    private ApiResponse<TodoListResponseApiDto> buildApiOkResponse(List<TodoResponseServiceDto> todoList) {
        TodoListResponseApiDto dto = TodoListResponseApiDto.of(todoList);
        return ApiResponse.ok(dto);
    }
}
