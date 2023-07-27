package todolist.domain.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.todo.dto.apidto.request.TodoCreateApiDto;
import todolist.domain.todo.dto.apidto.response.TodoListResponseApiDto;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.domain.todo.service.TodoService;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/todos")
public class TodoController {

    private final TodoService todoService;

    /**
     * 특정 기간의 할 일 목록을 조회합니다.
     * @param from 조회 시작일
     * @param to 조회 종료일
     * @param searchType 조회 타입 (START_DATE, DEAD_LINE, DONE_DATE)
     * @return 할 일 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<TodoListResponseApiDto>> getTodos(LocalDate from, LocalDate to, SearchType searchType){

        Long memberId = SecurityUtil.getCurrentId();

        List<TodoResponseServiceDto> todoList = todoService.findTodoList(memberId, from, to, searchType);

        ApiResponse<TodoListResponseApiDto> response = buildApiOkResponse(todoList);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTodo(@RequestBody TodoCreateApiDto dto){

        Long memberId = SecurityUtil.getCurrentId();

        TodoCreateServiceDto serviceDto = TodoCreateApiDto.toServiceDto(dto);

        todoService.saveTodo(memberId, serviceDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
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
