package todolist.global.exception.buinessexception.commonexception;

import org.springframework.http.HttpStatus;
import todolist.global.exception.buinessexception.planexception.PlanException;

public class DefaultException extends PlanException {

    public static final String CODE = "COMMON-500";

    public DefaultException(String message) {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
