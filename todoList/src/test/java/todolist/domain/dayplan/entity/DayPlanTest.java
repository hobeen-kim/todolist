package todolist.domain.dayplan.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.global.exception.buinessexception.planexception.PlanTimeValidException;
import todolist.global.exception.buinessexception.planexception.dayplanexception.DayPlanTodoDoneException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static todolist.domain.todo.entity.Importance.RED;

class DayPlanTest {

    @TestFactory
    @DisplayName("DayPlan 에서 Todo 를 추가한 후 제거합니다.")
    Collection<DynamicTest> addTodo() {
        //given
        Todo todo = createTodoDefault();
        DayPlan dayPlan = createDayPlanDefault();

        return List.of(
                dynamicTest("DayPlan 에서 Todo 를 추가합니다.", () ->{
                    //when
                    dayPlan.addTodo(todo);

                    //then
                    assertThat(dayPlan.getTodo()).isEqualTo(todo);
                    assertThat(todo.getDayPlans()).hasSize(1);
                }),
                dynamicTest("DayPlan 에서 Todo 를 삭제합니다.", () ->{
                    //when
                    dayPlan.removeTodo();

                    //then
                    assertThat(dayPlan.getTodo()).isNull();
                    assertThat(todo.getDayPlans()).hasSize(0);
                })
        );
    }

    @Test
    @DisplayName("DayPlan 에 Todo 를 추가할 때 Todo 가 완료되었으면 DayPlanTodoDoneException 예외를 발생시킨다.")
    void addTodoException() {
        //given
        Todo todo = createTodoDone();
        DayPlan dayPlan = createDayPlanDefault();

        //when //then
        DayPlanTodoDoneException exception = assertThrows(DayPlanTodoDoneException.class,
                () -> dayPlan.addTodo(todo));

        assertThat(exception.getMessage()).isEqualTo(DayPlanTodoDoneException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(DayPlanTodoDoneException.CODE);
    }

    @Test
    @DisplayName("member 의 addDayPlans() 메서드로 Member 와 DayPlan 의 연관관계를 맺는다.")
    void addMember(){
        //given
        Member member = createMember();
        DayPlan dayPlan = createDayPlanDefault();

        //when
        member.addDayPlans(dayPlan);

        //then
        assertThat(dayPlan.getMember()).isEqualTo(member);
        assertThat(member.getDayPlans()).hasSize(1);
    }

    @Test
    @DisplayName("content, date, startTime, endTime 을 인자로 받아 DayPlan 을 생성한다.")
    void createDayPlan() {
        //given
        String content = "test";
        LocalDate date = LocalDate.of(2023, 7, 20);
        LocalTime startTime = LocalTime.of(12, 0, 0);
        LocalTime endTime = LocalTime.of(12, 20, 0);

        //when
        DayPlan dayPlan = DayPlan.createDayPlan(content, date, startTime, endTime);

        //then
        assertThat(dayPlan.getContent()).isEqualTo(content);
        assertThat(dayPlan.getDate()).isEqualTo(date);
        assertThat(dayPlan.getStartTime()).isEqualTo(startTime);
        assertThat(dayPlan.getEndTime()).isEqualTo(endTime);
    }

    @Test
    @DisplayName("DayPlan 생성 시 startTime 보다 endTime 이 빠르면 DayPlanTimeValidException 예외를 발생시킨다.")
    void createDayPlanException(){
        //given
        String content = "test";
        LocalDate date = LocalDate.of(2023, 7, 20);
        LocalTime startTime = LocalTime.of(12, 20, 0);
        LocalTime endTime = LocalTime.of(12, 0, 0);

        //when //then
        PlanTimeValidException exception = assertThrows(PlanTimeValidException.class,
                () -> DayPlan.createDayPlan(content, date, startTime, endTime));

        assertThat(exception.getMessage()).isEqualTo(PlanTimeValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanTimeValidException.CODE);
    }

    @Test
    @DisplayName("DayPlan 의 content 를 변경한다.")
    void changeContent() {
        //given
        String changeContent = "change";
        DayPlan dayPlan = createDayPlanDefault();

        //when
        dayPlan.changeContent(changeContent);

        //then
        assertThat(dayPlan.getContent()).isEqualTo(changeContent);
    }

    @Test
    @DisplayName("DayPlan 의 date 를 변경한다.")
    void changeDate() {
        //given
        LocalDate date = LocalDate.of(2023, 6, 20);
        DayPlan dayPlan = createDayPlanDefault();

        //when
        dayPlan.changeDate(date);

        //then
        assertThat(dayPlan.getDate()).isEqualTo(date);

    }

    @TestFactory
    @DisplayName("DayPlan 의 완료 여부를 변경한다.")
    Collection<DynamicTest> idDone() {
        //given
        DayPlan dayPlan = createDayPlanDefault();

        return List.of(
            dynamicTest("완료 여부를 true 로 변경한다.", () -> {
                //given
                dayPlan.isDone(true);

                //when
                assertThat(dayPlan.isDone()).isTrue();
            }),
            dynamicTest("완료 여부를 false 로 변경한다.", () -> {
                //when
                dayPlan.isDone(false);

                //then
                assertThat(dayPlan.isDone()).isFalse();

            })
        );
    }

    @TestFactory
    @DisplayName("시작 시간과 완료 시간으로 DayPlan 의 시간을 변경한다.")
    Collection<DynamicTest> changeTime() {

        //given
        DayPlan dayPlan = createDayPlanDefault();


        return List.of(
                dynamicTest("startTime, endTime 으로 시작, 완료시간을 변경한다.", () -> {
                    //given
                    LocalTime startTime = LocalTime.of(13, 0, 0);
                    LocalTime endTime = LocalTime.of(13, 20, 0);

                    //when
                    dayPlan.changeTime(startTime, endTime);

                    //then
                    assertThat(dayPlan.getStartTime()).isEqualTo(startTime);
                    assertThat(dayPlan.getEndTime()).isEqualTo(endTime);
                }),
                dynamicTest("startTime 만으로 시작시간만 변경한다.", () -> {
                    //given
                    LocalTime startTime = LocalTime.of(12, 0, 0);
                    LocalTime endTime = LocalTime.from(dayPlan.getEndTime());

                    //when
                    dayPlan.changeTime(startTime, null);

                    //then
                    assertThat(dayPlan.getStartTime()).isEqualTo(startTime);
                    assertThat(dayPlan.getEndTime()).isEqualTo(endTime);
                }),
                dynamicTest("startTime 만으로 시작시간만 변경한다.", () -> {
                    //given
                    LocalTime startTime = LocalTime.from(dayPlan.getStartTime());
                    LocalTime endTime = LocalTime.of(14, 20, 0);

                    //when
                    dayPlan.changeTime(null, endTime);

                    //then
                    assertThat(dayPlan.getStartTime()).isEqualTo(startTime);
                    assertThat(dayPlan.getEndTime()).isEqualTo(endTime);
                })
        );
    }

    @Test
    @DisplayName("DayPlan 시간 변경 시 startTime 보다 endTime 이 빠르면 DayPlanTimeValidException 예외를 발생시킨다.")
    void changeTimeException() {
        //given
        DayPlan dayPlan = createDayPlanDefault();
        LocalTime startTime = LocalTime.of(12, 20, 0);
        LocalTime endTime = LocalTime.of(12, 0, 0);

        //when //then
        PlanTimeValidException exception = assertThrows(PlanTimeValidException.class,
                () -> dayPlan.changeTime(startTime, endTime));

        assertThat(exception.getMessage()).isEqualTo(PlanTimeValidException.MESSAGE);
        assertThat(exception.getErrorCode()).isEqualTo(PlanTimeValidException.CODE);
    }

    Member createMember(){
        return Member.builder()
                .name("test")
                .username("test")
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

    DayPlan createDayPlan(LocalDate date, LocalTime startTime, LocalTime endTime){
        return DayPlan.builder()
                .content("test")
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
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

    Todo createTodoDone(){
        return Todo.builder()
                .content("test")
                .importance(RED)
                .startDate(LocalDate.of(2023, 7, 20))
                .deadLine(LocalDate.of(2023, 7, 21))
                .doneDate(LocalDate.of(2023, 7, 21))
                .build();
    }
}