package todolist.global.exception.buinessexception.planexception;

import org.springframework.http.HttpStatus;

public class PlanAccessDeniedException extends PlanException {

    public static final String MESSAGE = "접근 권한이 없는 일정입니다.";
    public static final String CODE = "PLAN-403";

    public PlanAccessDeniedException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}