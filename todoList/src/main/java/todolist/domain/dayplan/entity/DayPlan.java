package todolist.domain.dayplan.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.category.entity.Category;
import todolist.domain.member.entity.Member;
import todolist.domain.todo.entity.Todo;
import todolist.global.entity.PlanEntity;
import todolist.global.exception.buinessexception.planexception.PlanTimeValidException;
import todolist.global.exception.buinessexception.planexception.dayplanexception.DayPlanTodoDoneException;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
public class DayPlan extends PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Builder
    private DayPlan(Member member, Category category, String content, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.member = member;
        this.category = category;
        this.content = content;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //==연관관계 메서드==//
    /**
     * DayPlan과 Todo의 연관관계를 설정한다. DayPlan에서 todo 를 추가하도록 한다. (웬만해선 todo 가 먼저 추가되므로.)
     * @param todo : 추가할 todo
     */
    public void addTodo(Todo todo) {
        if(todo.isDone()) throw new DayPlanTodoDoneException();
        this.todo = todo;
        todo.addDayPlan(this);
    }

    public void removeTodo(){
        this.todo.removeDayPlan(this);
        this.todo = null;
    }

    public void changeCategory(Category category) {
        this.category = category;
        category.addDayPlan(this);
    }


    //==생성 메서드==//
    public static DayPlan createDayPlan(Member member, Category category, String content, LocalDate date, LocalTime startTime, LocalTime endTime) {

        validateDate(startTime, endTime);

        return new DayPlan(member, category, content, date, startTime, endTime);
    }

    //==비즈니스 로직==//
    public void changeContent(String content) {
        this.content = content;
    }

    public void isDone(boolean isDone) {
        this.isDone = isDone;
    }

    public void changeDate(LocalDate date) {
        this.date = date;
    }

    public void changeTime(LocalTime startTime, LocalTime endTime) {
        startTime = startTime == null ? this.startTime : startTime;
        endTime = endTime == null ? this.endTime : endTime;
        validateDate(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 시작시간과 종료시간을 검증한다.
     * @param startTime 시작시간
     * @param endTime 종료시간
     */
    private static void validateDate(LocalTime startTime, LocalTime endTime) {

        if (startTime.isAfter(endTime)) throw new PlanTimeValidException();
    }


}
