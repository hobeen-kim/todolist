package todolist.global.exception.buinessexception.planexception.dayplanexception;

import org.springframework.http.HttpStatus;
import todolist.global.exception.buinessexception.planexception.PlanException;

public class DayPlanTodoDoneException extends PlanException {

    public static final String MESSAGE = "완료된 Todo 입니다.";
    public static final String CODE = "DAYPLAN-400";
    public DayPlanTodoDoneException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
