package todolist.global.exception.buinessexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private BindingResult errors;

    protected BusinessException(String errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected BusinessException(String errorCode, HttpStatus httpStatus, String message, BindingResult errors) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errors = errors;
    }
}
