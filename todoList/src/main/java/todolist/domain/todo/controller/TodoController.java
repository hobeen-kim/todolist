package todolist.domain.todo.controller;

import com.google.protobuf.Api;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import todolist.auth.utils.SecurityUtil;
import todolist.domain.dayplan.dto.apidto.response.DayPlanListResponseApiDto;
import todolist.domain.todo.dto.apidto.request.TodoCreateApiDto;
import todolist.domain.todo.dto.apidto.request.TodoUpdateApiDto;
import todolist.domain.todo.dto.apidto.response.TodoListResponseApiDto;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoUpdateServiceDto;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.domain.todo.service.TodoService;
import todolist.global.reponse.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/todos")
@Validated
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
    public ResponseEntity<ApiResponse<TodoListResponseApiDto>> getTodos(
            Long categoryId,
            LocalDate from,
            LocalDate to,
            @NotNull(message = "{validation.searchType}") SearchType searchType){

        Long memberId = SecurityUtil.getCurrentId();

        List<TodoResponseServiceDto> todoList = todoService.findTodoList(memberId, categoryId, from, to, searchType);

        ApiResponse<TodoListResponseApiDto> response = buildApiOkResponse(todoList);

        return ResponseEntity.ok(response);
    }

    /**
     * todo 를 생성합니다.
     * @param dto 생성에 필요한 정보
     * @return 201 응답만 반환합니다.
     */
    @PostMapping
    public ResponseEntity<Void> createTodo(@RequestBody @Valid TodoCreateApiDto dto){

        Long memberId = SecurityUtil.getCurrentId();

        todoService.saveTodo(memberId, dto.toServiceDto());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<Void> updateTodo(@RequestBody @Valid TodoUpdateApiDto dto, @PathVariable Long todoId){

        Long memberId = SecurityUtil.getCurrentId();

        todoService.updateTodo(memberId, dto.toServiceDto(todoId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Api> deleteTodo(@PathVariable Long todoId){

        Long memberId = SecurityUtil.getCurrentId();

        todoService.deleteTodo(memberId, todoId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private ApiResponse<TodoListResponseApiDto> buildApiOkResponse(List<TodoResponseServiceDto> todoList) {
        TodoListResponseApiDto dto = TodoListResponseApiDto.of(todoList);
        return ApiResponse.ok(dto);
    }
}
