package todolist.global.exception.buinessexception.planexception;

import org.springframework.http.HttpStatus;

public class PlanTimeValidException extends PlanException {

    public static final String MESSAGE = "시간을 다시 확인해주세요.";
    public static final String CODE = "PLAN-400";

    public PlanTimeValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
