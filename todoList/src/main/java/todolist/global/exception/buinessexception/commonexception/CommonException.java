package todolist.global.exception.buinessexception.commonexception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import todolist.global.exception.buinessexception.BusinessException;

public abstract class CommonException extends BusinessException {

    protected CommonException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }

    protected CommonException(String errorCode, HttpStatus httpStatus, String message, BindingResult errors) {
        super(errorCode, httpStatus, message, errors);
    }
}
