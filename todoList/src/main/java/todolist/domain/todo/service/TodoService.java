package todolist.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todolist.auth.service.CustomUserDetailsService;
import todolist.domain.member.entity.Member;
import todolist.domain.member.service.MemberService;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.repository.searchCond.DateTypeSearchCond;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoUpdateServiceDto;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.TodoRepository;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.global.exception.buinessexception.planexception.PlanNotFoundException;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final CustomUserDetailsService userDetailsService;


    /**
     * Todo 를 생성하고 저장합니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param dto Todo 생성에 필요한 정보
     * @return 생성된 Todo
     */
    @Transactional
    public TodoResponseServiceDto saveTodo(Long memberId, TodoCreateServiceDto dto) {
        Todo todo = Todo.createTodo(
                dto.getContent(),
                dto.getImportance(),
                dto.getStartDate(),
                dto.getDeadLine()
        );

        Member member = verifiedMember(memberId);
        member.addTodos(todo);
        todoRepository.save(todo);

        return TodoResponseServiceDto.of(todo);
    }


    /**
     * id 를 통해 Todo 를 찾습니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param todoId 찾을 Todo 의 id
     * @return 찾은 Todo
     */
    public TodoResponseServiceDto findTodo(Long memberId, Long todoId) {

        return TodoResponseServiceDto.of(verifiedTodo(memberId, todoId));
    }


    /**
     * 특정 일자 사이의 Todo 를 리스트형태로 찾습니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param from 시작일자
     * @param to 종료일자
     * @param searchType 검색 타입(시작예정일, 마감예정일, 완료일)
     * @return 찾은 Todo 리스트
     */
    public List<TodoResponseServiceDto> findTodoList(Long memberId, LocalDate from, LocalDate to, SearchType searchType) {

        DateTypeSearchCond cond = new DateTypeSearchCond(from, to, searchType);

        List<Todo> todos = todoRepository.findByCond(memberId, cond);
        return TodoResponseServiceDto.of(todos);
    }

    /**
     * dto 로 받은 정보로 Todo 를 수정합니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param dto 수정할 Todo 의 정보
     */
    @Transactional
    public void updateTodo(Long memberId, TodoUpdateServiceDto dto){
        Todo todo = verifiedTodo(memberId, dto.getId());
        update(todo, dto);
    }

    /**
     * Todo 를 삭제합니다.
     * @param memberId 스레드로컬 Member 의 id
     * @param todoId 삭제할 Todo 의 id
     */
    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        verifiedTodo(memberId, todoId);
        todoRepository.deleteById(todoId);
    }

    private void update(Todo todo, TodoUpdateServiceDto dto) {
        if(dto.getContent() != null) todo.changeContent(dto.getContent());
        if(dto.getImportance() != null) todo.changeImportance(dto.getImportance());
        if(dto.getDoneDate() != null) todo.isDone(dto.getDoneDate());
        todo.changeDate(dto.getStartDate(), dto.getDeadLine());
    }

    private Member verifiedMember(Long memberId){
        return userDetailsService.loadUserById(memberId);
    }

    private Todo verifiedTodo(Long memberId, Long todoId){
        Todo todo = todoRepository.findByIdWithMember(todoId)
                .orElseThrow(PlanNotFoundException::new);

        if(!todo.getMember().getId().equals(memberId)){
            throw new PlanAccessDeniedException();
        }

        return todo;
    }
}
