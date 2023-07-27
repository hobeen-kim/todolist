package todolist.global.exception.buinessexception.planexception;

import org.springframework.http.HttpStatus;

public class PlanDateValidException extends PlanException {

    public static final String MESSAGE = "날짜를 다시 확인해주세요.";
    public static final String CODE = "PLAN-400";

    public PlanDateValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
