package todolist.global.exception.buinessexception.planexception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import todolist.global.exception.buinessexception.BusinessException;

public abstract class PlanException extends BusinessException {

    protected PlanException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }

    protected PlanException(String errorCode, HttpStatus httpStatus, String message, BindingResult errors) {
        super(errorCode, httpStatus, message, errors);
    }



}
