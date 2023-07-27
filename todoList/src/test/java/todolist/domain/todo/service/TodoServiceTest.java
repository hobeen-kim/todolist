package todolist.domain.todo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Member;
import todolist.domain.member.repository.MemberRepository;
import todolist.domain.todo.dto.servicedto.TodoCreateServiceDto;
import todolist.domain.todo.dto.servicedto.TodoResponseServiceDto;
import todolist.domain.todo.dto.servicedto.TodoUpdateServiceDto;
import todolist.domain.todo.entity.Importance;
import todolist.domain.todo.entity.Todo;
import todolist.domain.todo.repository.TodoRepository;
import todolist.domain.todo.repository.searchCond.SearchType;
import todolist.global.ServiceTest;
import todolist.global.exception.buinessexception.planexception.PlanAccessDeniedException;
import todolist.global.exception.buinessexception.planexception.PlanDateValidException;
import todolist.global.exception.buinessexception.planexception.PlanNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static todolist.domain.todo.entity.Importance.BLUE;
import static todolist.domain.todo.entity.Importance.RED;

class TodoServiceTest extends ServiceTest {

    @Autowired TodoService todoService;
    @Autowired MemberRepository memberRepository;
    @Autowired TodoRepository todoRepository;

    @Test
    @DisplayName("memberId 와 생성정보를 받아 Todo 를 생성한다.")
    void saveTodo() {
        //given
        TodoCreateServiceDto dto = TodoCreateServiceDto.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 21))
                .deadLine(LocalDate.of(2023, 7, 22))
                .build();

        Member savedMember = memberRepository.save(createMemberDefault());

        //when
        TodoResponseServiceDto todoDto = todoService.saveTodo(savedMember.getId(), dto);

        //then
        assertThat(todoDto.getContent()).isEqualTo(dto.getContent());
        assertThat(todoDto.getImportance()).isEqualTo(dto.getImportance());
        assertThat(todoDto.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(todoDto.getDeadLine()).isEqualTo(dto.getDeadLine());
        assertThat(savedMember.getTodos()).hasSize(1);
    }

    @Test
    @DisplayName("Todo 생성 시 시작예정일보다 마감예정일이 빠르면 PlanDateValidException 이 발생한다.")
    void saveTodoException() {
        //given
        TodoCreateServiceDto todoCreateServiceDto = TodoCreateServiceDto.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 21))
                .deadLine(LocalDate.of(2023, 7, 20))
                .build();

        Member savedMember = memberRepository.save(createMemberDefault());

        //when
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                () -> todoService.saveTodo(savedMember.getId(), todoCreateServiceDto));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);

    }

    @Test
    @DisplayName("Todo id 를 통해 Todo 를 찾는다.")
    void findTodo() {
        //given
        Member member = createMemberDefault();
        Todo todo = createTodoDefault();
        member.addTodos(todo);

        memberRepository.save(member);

        //when
        TodoResponseServiceDto findTodoDto = todoService.findTodo(member.getId(), todo.getId());

        //then
        assertThat(findTodoDto.getContent()).isEqualTo(todo.getContent());
        assertThat(findTodoDto.getImportance()).isEqualTo(todo.getImportance());
        assertThat(findTodoDto.getDeadLine()).isEqualTo(todo.getDeadLine());
    }

    @Test
    @DisplayName("다른 멤버의 todo 에 접근할 경우 PlanAccessDeniedException 을 발생시킨다.")
    void findTodoException1() {
        //given
        Member member = createMemberDefault();
        Todo todo = createTodoDefault();
        member.addTodos(todo);

        memberRepository.save(member);

        //when
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class,
                () -> todoService.findTodo(member.getId() + 1, todo.getId()));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }

    @Test
    @DisplayName("Todo 를 찾을 때 todoId 가 없을 경우 PlanNotFoundException 을 발생시킨다.")
    void findTodoException2() {
        //given
        Member member = createMemberDefault();
        Todo todo = createTodoDefault();
        member.addTodos(todo);

        memberRepository.save(member);

        //when
        PlanNotFoundException exception = assertThrows(PlanNotFoundException.class,
                () -> todoService.findTodo(member.getId(), todo.getId() + 1));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanNotFoundException.CODE);
    }

    @TestFactory
    @DisplayName("시작, 종료 날짜를 입력받아 해당 날짜 사이에 있는 todo 리스트를 찾는다.")
    Collection<DynamicTest> findAll() {
        //given
        Member member = createMemberDefault();
        List<Todo> todos = createTodos(50);
        addTodos(member, todos);

        memberRepository.save(member);

        return List.of(
                dynamicTest("검색 조건을 start date 로 한다.",()->{
                    //given
                    LocalDate from = LocalDate.of(2023, 3, 1);
                    LocalDate to = LocalDate.of(2023, 3, 31);
                    SearchType searchType = SearchType.START_DATE;

                    //when
                    List<TodoResponseServiceDto> todoDtoList = todoService.findTodoList(member.getId(), from, to, searchType);

                    //then
                    assertThat(todoDtoList).hasSize(31);
                    assertThat(todoDtoList.get(0))
                            .extracting("startDate")
                            .isEqualTo(LocalDate.of(2023, 3, 1));
                    assertThat(todoDtoList.get(todoDtoList.size() - 1))
                            .extracting("startDate")
                            .isEqualTo(LocalDate.of(2023, 3, 31));
                }),
                dynamicTest("검색 조건을 deadLine 으로 한다.",()->{
                    //given
                    LocalDate from = LocalDate.of(2023, 4, 1);
                    LocalDate to = LocalDate.of(2023, 4, 30);
                    SearchType searchType = SearchType.DEAD_LINE;

                    //when
                    List<TodoResponseServiceDto> todoDtoList = todoService.findTodoList(member.getId(), from, to, searchType);

                    //then
                    assertThat(todoDtoList).hasSize(30);
                    assertThat(todoDtoList.get(0))
                            .extracting("deadLine")
                            .isEqualTo(LocalDate.of(2023, 4, 1));
                    assertThat(todoDtoList.get(todoDtoList.size() - 1))
                            .extracting("deadLine")
                            .isEqualTo(LocalDate.of(2023, 4, 30));
                }),
                dynamicTest("검색 조건을 doneDate 로 한다.",()->{
                    //given
                    LocalDate from = LocalDate.of(2023, 3, 15);
                    LocalDate to = LocalDate.of(2023, 3, 31);
                    SearchType searchType = SearchType.DONE_DATE;

                    //when
                    List<TodoResponseServiceDto> todoDtoList = todoService.findTodoList(member.getId(), from, to, searchType);

                    //then
                    assertThat(todoDtoList).hasSize(17);
                    assertThat(todoDtoList.get(0))
                            .extracting("doneDate")
                            .isEqualTo(LocalDate.of(2023, 3, 15));
                    assertThat(todoDtoList.get(todoDtoList.size() - 1))
                            .extracting("doneDate")
                            .isEqualTo(LocalDate.of(2023, 3, 31));
                }),
                dynamicTest("검색 조건에 날짜가 둘 다 null 이면 전부 검색한다.",()->{
                    //given
                    LocalDate from = null;
                    LocalDate to = null;
                    SearchType searchType = SearchType.DONE_DATE;

                    //when
                    List<TodoResponseServiceDto> todoDtoList = todoService.findTodoList(member.getId(), from, to, searchType);

                    //then
                    assertThat(todoDtoList).hasSize(todos.size());
                })
        );

    }

    @Test
    @DisplayName("업데이트 정보를 받아 todo 를 업데이트 한다.")
    void updateTodo() {
        //given
        Member member = createMemberDefault();
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo("test", RED, startDate, deadline);

        member.addTodos(todo);
        memberRepository.save(member);

        TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                .id(todo.getId())
                .content("update")
                .importance(BLUE)
                .startDate(startDate.plusDays(2))
                .deadLine(deadline.plusDays(2))
                .build();

        //when
        todoService.updateTodo(member.getId(), dto);

        //then
        Todo findTodo = em.find(Todo.class, todo.getId());
        assertThat(findTodo.getContent()).isEqualTo("update");
        assertThat(findTodo.getImportance()).isEqualTo(BLUE);
        assertThat(findTodo.getStartDate()).isEqualTo(startDate.plusDays(2));
        assertThat(findTodo.getDeadLine()).isEqualTo(deadline.plusDays(2));
    }


    @Test
    @DisplayName("Todo 의 startDate 를 변경할 때 startDate 가 deadLine 보다 늦을 경우 TodoStartDateException 을 발생시킨다.")
    void changeStartDateException() {
        //given
        Member member = createMemberDefault();
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo(startDate, deadline);

        member.addTodos(todo);
        memberRepository.save(member);

        TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                .id(todo.getId())
                .startDate(deadline.plusDays(1))
                .build();

        //when
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                () -> todoService.updateTodo(member.getId(), dto));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);
    }

    @Test
    @DisplayName("Todo 의 deadLine 를 변경할 때 deadLine 이 startDate 보다 빠를 경우 TodoStartDateException 을 발생시킨다.")
    void changeDeadLineException() {
        //given
        Member member = createMemberDefault();
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo(startDate, deadline);

        member.addTodos(todo);
        memberRepository.save(member);

        TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                .id(todo.getId())
                .deadLine(startDate.minusDays(1))
                .build();

        //when
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                () -> todoService.updateTodo(member.getId(), dto));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);
    }

    @Test
    @DisplayName("Todo 업데이트 시 todo 의 memberId 와 요청한 memberId 가 다를 경우 PlanAccessDeniedException 이 발생한다.")
    void updateTodoException() {
        //given
        Member member = createMemberDefault();
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo("test", RED, startDate, deadline);

        member.addTodos(todo);
        memberRepository.save(member);

        TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                .id(todo.getId())
                .content("update")
                .importance(BLUE)
                .startDate(startDate.plusDays(2))
                .deadLine(deadline.plusDays(2))
                .build();

        //when //then
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class,
                () -> todoService.updateTodo(member.getId() + 1, dto));//다른 멤버

        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }
    
    @Test
    @DisplayName("Todo id 를 통해 Todo 를 삭제한다. 연관된 DayPlan 이 있으면 모두 삭제된다.")
    void deleteTodo() {
        //given
        Member member = createMemberDefault();
        Todo todo = createTodoDefault();
        DayPlan dayPlan = createDayPlanDefault();

        member.addTodos(todo);
        member.addDayPlans(dayPlan);
        dayPlan.addTodo(todo);

        memberRepository.save(member);
    
        //when
        todoService.deleteTodo(member.getId(), todo.getId());

        //then
        Todo findTodo = em.find(Todo.class, todo.getId());
        DayPlan findDayPlan = em.find(DayPlan.class, dayPlan.getId());

        assertThat(findTodo).isNull();
        assertThat(findDayPlan).isNull();
    }

    @Test
    @DisplayName("Todo 삭제 시 todo 의 memberId 와 요청한 memberId 가 다를 경우 PlanAccessDeniedException 이 발생한다.")
    void deleteTodoException1() {
        //given
        Member member = createMemberDefault();
        Todo todo = createTodoDefault();

        member.addTodos(todo);
        memberRepository.save(member);

        //when
        todoService.deleteTodo(member.getId(), todo.getId());

        //when
        PlanAccessDeniedException exception = assertThrows(PlanAccessDeniedException.class,
                () -> todoService.deleteTodo(member.getId() + 1L, todo.getId()));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanAccessDeniedException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanAccessDeniedException.CODE);
    }

    @Test
    @DisplayName("Todo 삭제 시 요청한 todo id 가 없을 경우 PlanNotFoundException 이 발생한다.")
    void deleteTodoException2() {
        //given
        Member member = createMemberDefault();
        Long todoId = 1L; //없는 아이디
        memberRepository.save(member);

        //when
        PlanNotFoundException exception = assertThrows(PlanNotFoundException.class,
                () -> todoService.deleteTodo(member.getId(), todoId));

        //then
        assertThat(exception.getMessage()).isEqualTo(PlanNotFoundException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanNotFoundException.CODE);
    }

    Member createMemberDefault() {
        return Member.builder()
                .name("test")
                .username("test")
                .build();
    }

    Todo createTodoDefault(){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .deadLine(LocalDate.of(2023, 7, 21))
                .build();
    }

    Todo createTodo(String content, Importance importance, LocalDate startDate, LocalDate deadLine){
        Todo todo = Todo.builder()
                .content(content)
                .importance(importance)
                .startDate(startDate)
                .deadLine(deadLine)
                .build();

        return todo;
    }

    Todo createTodo(LocalDate startDate, LocalDate deadLine){
        Todo todo = Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(startDate)
                .deadLine(deadLine)
                .build();

        return todo;
    }

    List<Todo> createTodos(int count){
        List<Todo> todos = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate deadline = LocalDate.of(2023, 4, 1);
        LocalDate doneDate = LocalDate.of(2023, 3, 15);
        for (int i = 0; i < count; i++) {
            Importance importance = Importance.values()[i % 3];
            Todo todo = createTodo("content " + (i + 1), importance, startDate.plusDays(i), deadline.plusDays(i));
            todo.isDone(doneDate.plusDays(i));
            todos.add(todo);
        }
        return todos;
    }

    void addTodos(Member member, List<Todo> todos){
        for (Todo todo : todos) {
            member.addTodos(todo);
        }
    }

    DayPlan createDayPlanDefault(){
        return DayPlan.builder()
                .content("test")
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .build();
    }
}