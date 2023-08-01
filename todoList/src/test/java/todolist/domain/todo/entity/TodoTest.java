package todolist.domain.todo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import todolist.domain.category.entity.Category;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.domain.member.entity.Authority;
import todolist.domain.member.entity.Member;
import todolist.domain.toplist.entity.TopList;
import todolist.global.exception.buinessexception.planexception.PlanDateValidException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.DynamicTest.*;
import static todolist.domain.todo.entity.Importance.BLUE;
import static todolist.domain.todo.entity.Importance.RED;

class TodoTest {

    @Test
    @DisplayName("member 의 addTodos() 메서드로 Member 와 Todo 의 연관관계를 맺는다.")
    void setMember() {
        //given
        Member member = createMember();
        Todo todo = createTodoDefault();

        //when
        member.addTodos(todo);

        //then
        assertThat(todo.getMember()).isEqualTo(member);
        assertThat(member.getTodos()).hasSize(1);
    }

    @Test
    @DisplayName("todo 에 TopList 를 추가한다.")
    void addTopList() {
        //given
        Todo todo = createTodoDefault();
        TopList topList = createTopList();

        //when
        todo.addTopList(topList);

        //then
        assertThat(todo.getTopList()).isEqualTo(topList);
        assertThat(topList.getTodos()).hasSize(1);

    }

    @TestFactory
    @DisplayName("Todo 에 DayPlan 을 추가한 후 삭제한다.")
    Collection<DynamicTest> addDayPlans() {
        //given
        Todo todo = createTodoDefault();
        DayPlan dayPlan = createDayPlanDefault();

        return List.of(
                dynamicTest("DayPlan 을 추가한다.", () -> {
                    //when
                    todo.addDayPlan(dayPlan);

                    //then
                    assertThat(todo.getDayPlans()).hasSize(1);
                    assertThat(dayPlan.getTodo()).isNull(); // DayPlan 에 Todo 가 추가되었지만 Todo 에 DayPlan 이 추가되지 않습니다.
                }),
                dynamicTest("추가된 DayPlan 을 삭제한다.", () -> {
                    //when
                    todo.removeDayPlan(dayPlan);

                    //then
                    assertThat(todo.getDayPlans()).hasSize(0);
                })
        );

    }

    @Test
    @DisplayName("Todo 의 createTodo() 메서드로 Todo 를 생성한다.")
    void createTodo() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        String content = "test";
        Importance importance = RED;
        LocalDate startDate = LocalDate.of(2023, 7, 21);
        LocalDate deadline = LocalDate.of(2023, 7, 21);

        //when
        Todo todo = Todo.createTodo(member, category, content, importance, startDate, deadline);

        //then
        assertThat(todo.getMember()).isEqualTo(member);
        assertThat(todo.getCategory()).isEqualTo(category);
        assertThat(todo.getContent()).isEqualTo(content);
        assertThat(todo.getImportance()).isEqualTo(importance);
        assertThat(todo.getStartDate()).isEqualTo(startDate);
        assertThat(todo.getDeadLine()).isEqualTo(deadline);
    }

    @Test
    @DisplayName("Todo 를 만들 때 startDate 보다 deadline 이 빠르면 TodoDateValidException 예외를 발생시킨다.")
    void createTodoException() {
        //given
        Member member = createMember();
        Category category = createCategory(member);
        String content = "test";
        Importance importance = RED;
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 21);

        //when //then
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                ()->Todo.createTodo(member, category, content, importance, startDate, deadline));

        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);

    }

    @Test
    @DisplayName("Todo 의 changeImportance() 메서드로 중요도를 변경한다.")
    void changeImportance() {
        //given
        Todo todo = createTodoDefault();

        //when
        todo.changeImportance(BLUE);

        //then
        assertThat(todo.getImportance()).isEqualTo(BLUE);
    }

    @Test
    @DisplayName("Todo 의 changeContent() 메서드로 내용을 변경한다.")
    void changeContent() {
        //given
        Todo todo = createTodoDefault();

        //when
        todo.changeContent("change");

        //then
        assertThat(todo.getContent()).isEqualTo("change");
    }

    @TestFactory
    @DisplayName("Todo 의 isDone() 메서드로 완료처리를 한다.")
    Collection<DynamicTest> doneAt() {
        //given
        Todo todo = createTodoDefault();

        return List.of(
            dynamicTest("isDone 값으로 날짜를 지정하면 완료처리가 된다.",() -> {
                //when
                todo.isDone(LocalDate.of(2023, 7, 21));

                //then
                assertThat(todo.isDone()).isTrue();
                assertThat(todo.getDoneDate()).isEqualTo(LocalDate.of(2023, 7, 21));
        }),
            dynamicTest("완료처리된 Todo 에 날짜를 null 값으로 주면 미완료로 변경된다.",() -> {
                //when
                todo.isDone(null);

                //then
                assertThat(todo.isDone()).isFalse();
                assertThat(todo.getDoneDate()).isNull();
        })
        );
    }

    @Test
    @DisplayName("Todo 의 changeDate() 메서드로 시작예정일과 마감예정일을 변경한다.")
    void changeStartDate() {
        //given
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo(startDate, deadline);

        //when
        todo.changeDate(startDate.minusDays(1), deadline.minusDays(1));

        //then
        assertThat(todo.getStartDate()).isEqualTo(startDate.minusDays(1));
        assertThat(todo.getDeadLine()).isEqualTo(deadline.minusDays(1));
    }

    @Test
    @DisplayName("Todo 의 StartDate 를 변경할 때 deadline 보다 느리면 TodoDateValidException 예외를 발생시킨다.")
    void changeStartDateException() {
        //given
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo(startDate, deadline);

        //when //then
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                ()->todo.changeDate(deadline.plusDays(1), null));

        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);

    }

    @Test
    @DisplayName("Todo 의 deadline 를 변경할 때 startdate 보다 빠르멸 TodoDateValidException 예외를 발생시킨다.")
    void changeDeadLineException() {
        //given
        LocalDate startDate = LocalDate.of(2023, 7, 22);
        LocalDate deadline = LocalDate.of(2023, 7, 23);
        Todo todo = createTodo(startDate, deadline);

        //when //then
        PlanDateValidException exception = assertThrows(PlanDateValidException.class,
                ()->todo.changeDate(null, startDate.minusDays(1)));

        assertThat(exception.getMessage()).isEqualTo(PlanDateValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanDateValidException.CODE);

    }
    
    Member createMember(){
        return Member.builder()
                .name("test")
                .username("test")
                .build();
                
    }

    protected Member createMemberDefault() {
        return Member.builder()
                .name("test")
                .username("test")
                .password("1234abcd!")
                .authority(Authority.ROLE_USER)
                .email("email@test.com")
                .build();
    }

    protected Category createCategory(Member member) {
        return Category.builder()
                .categoryName("category")
                .hexColor("#FFFFFF")
                .member(member)
                .build();
    }

    Todo createTodoDefault(){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .build();
    }

    Todo createTodo(LocalDate startDate, LocalDate deadline){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(startDate)
                .deadLine(deadline)
                .build();
    }

    DayPlan createDayPlanDefault(){
        return DayPlan.builder()
                .content("test")
                .date(LocalDate.of(2023, 7, 20))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(12, 20, 0))
                .build();
    }

    TopList createTopList(){
        return TopList.builder()
                .title("title")
                .content("content")
                .build();
    }

    protected Todo createTodo(Member member, Category category){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .member(member)
                .category(category)
                .build();
    }


}