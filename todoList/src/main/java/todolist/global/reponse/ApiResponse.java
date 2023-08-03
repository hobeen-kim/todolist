package todolist.global.reponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import todolist.global.exception.buinessexception.BusinessException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * API 응답을 위한 공통 포맷
 * @param <T> data
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private T data;
    private int code; // ex. 200, 400, 401, 403, 404, 500
    private String status; // ex. "OK", "CREATED", "TODO-403" (에러의 경우)
    private String message; //ex. "success", "fail"

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.of(data, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus) {
        return ApiResponse.of(data, httpStatus, httpStatus.getReasonPhrase());
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus, String message) {
        return new ApiResponse<>(data, httpStatus.value(), httpStatus.name(), message);
    }

    public static ApiResponse<BindingResult> fail(BusinessException exception) {
        return new ApiResponse<>(
                exception.getErrors(),
                exception.getHttpStatus().value(),
                exception.getErrorCode(),
                exception.getMessage());
    }

    public static ApiResponse<List<ErrorResponse>> fail(MethodArgumentNotValidException exception) {
        return new ApiResponse<>(
                ErrorResponse.of(exception.getFieldErrors()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "입력 값을 확인해주세요."
        );
    }

    public static ApiResponse<List<ErrorResponse>> fail(ConstraintViolationException exception) {
        return new ApiResponse<>(
                ErrorResponse.of(exception.getConstraintViolations()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "입력 값을 확인해주세요."
        );
    }

    public static ApiResponse<Void> fail(HttpRequestMethodNotSupportedException exception) {
        return new ApiResponse<>(
                null,
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                HttpStatus.METHOD_NOT_ALLOWED.name(),
                "요청 url 과 method 를 확인해주세요."
        );
    }

    public static ApiResponse<BindingResult> fail(Exception exception) {
        return new ApiResponse<>(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                exception.getMessage()
        );
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorResponse {

        private String field;
        private String value;
        private String reason;

        public static List<ErrorResponse> of(List<FieldError> fieldErrors) {

            return fieldErrors.stream().map(fieldError -> new ErrorResponse(
                    fieldError.getField(),
                    Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString(),
                    fieldError.getDefaultMessage()
            )).toList();
        }

        public static List<ErrorResponse> of(Set<ConstraintViolation<?>> violations) {

           return violations.stream().map(violation -> new ErrorResponse(
                   ((PathImpl) violation.getPropertyPath()).getLeafNode().getName(),
                    Optional.ofNullable(violation.getInvalidValue()).orElse("null").toString(),
                    violation.getMessage()
            )).toList();
        }

    }


}
