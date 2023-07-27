package todolist.global.exception.buinessexception.authexception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import todolist.global.exception.buinessexception.BusinessException;

public abstract class AuthException extends BusinessException {

    protected AuthException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }

    protected AuthException(String errorCode, HttpStatus httpStatus, String message, BindingResult errors) {
        super(errorCode, httpStatus, message, errors);
    }
}
