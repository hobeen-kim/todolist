package todolist.domain.todo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todolist.domain.dayplan.entity.DayPlan;
import todolist.global.entity.PlanEntity;
import todolist.global.exception.buinessexception.planexception.PlanDateValidException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Todo extends PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private Importance importance = Importance.WHITE;

    private LocalDate startDate;

    private LocalDate deadLine;

    private LocalDate doneDate;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE)
    private List<DayPlan> dayPlans = new ArrayList<>();

    private Todo(String content, Importance importance, LocalDate startDate, LocalDate deadLine) {
        this.content = content;
        this.importance = importance;
        this.startDate = startDate;
        this.deadLine = deadLine;
    }

    @Builder
    private Todo(String content, Importance importance, LocalDate startDate, LocalDate deadLine, LocalDate doneDate) {
        this.content = content;
        this.importance = importance;
        this.startDate = startDate;
        this.deadLine = deadLine;
        this.doneDate = doneDate;
        this.isDone = doneDate != null;
    }

    //==연관 관계 메서드==///
    public void addDayPlan(DayPlan dayPlan){
        this.dayPlans.add(dayPlan);
    }

    public void removeDayPlan(DayPlan dayPlan){
        this.dayPlans.remove(dayPlan);
    }

    //==생성 메서드==//
    public static Todo createTodo(String content, Importance importance, LocalDate startDate, LocalDate deadLine) {

        validateDate(startDate, deadLine);

        return new Todo(content, importance, startDate, deadLine);
    }

    //==비즈니스 로직==//

    public void changeImportance(Importance importance) {
        this.importance = importance;
    }
    public void changeContent(String content) {
        this.content = content;
    }

    public void isDone(LocalDate doneDate) {
        this.isDone = doneDate != null;
        this.doneDate = doneDate;
    }

    public void changeDate(LocalDate startDate, LocalDate deadLine) {
        startDate = startDate == null ? this.startDate : startDate;
        deadLine = deadLine == null ? this.deadLine : deadLine;
        validateDate(startDate, deadLine);
        this.startDate = startDate;
        this.deadLine = deadLine;
    }

    /**
     * 시작일과 마감일을 검증한다.
     * @param startDate 시작일
     * @param deadLine 마감일
     */
    private static void validateDate(LocalDate startDate, LocalDate deadLine) {

        if (startDate.isAfter(deadLine)) throw new PlanDateValidException();
    }

}
