package todolist.global.exception.exceptionhandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import todolist.global.reponse.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        //첫 번째 에러 메시지만 가져옴
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return new ResponseEntity<>(
                ApiResponse.of(null, HttpStatus.BAD_REQUEST, message),
                HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException e) {

        //첫 번째 에러 메시지만 가져옴
        String message = e.getConstraintViolations().iterator().next().getMessage();

        return new ResponseEntity<>(
                ApiResponse.of(null, HttpStatus.BAD_REQUEST, message),
                HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<BindingResult>> handleException(Exception e) {

        e.printStackTrace();

        return new ResponseEntity<>(ApiResponse.fail(e), HttpStatusCode.valueOf(500));
    }
}
