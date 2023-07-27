package todolist.global.exception.buinessexception.planexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PlanNotFoundException extends PlanException {

    public static final String MESSAGE = "삭제되었거나 존재하지 않는 일정입니다.";
    public static final String CODE = "PLAN-401";

    public PlanNotFoundException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
