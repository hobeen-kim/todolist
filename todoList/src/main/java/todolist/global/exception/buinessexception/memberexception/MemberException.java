package todolist.global.exception.buinessexception.memberexception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import todolist.global.exception.buinessexception.BusinessException;

public abstract class MemberException extends BusinessException {

    protected MemberException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }

    protected MemberException(String errorCode, HttpStatus httpStatus, String message, BindingResult errors) {
        super(errorCode, httpStatus, message, errors);
    }



}
