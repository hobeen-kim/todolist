package todolist.global.reponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import todolist.global.exception.buinessexception.BusinessException;

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

    public static ApiResponse<BindingResult> fail(MethodArgumentNotValidException exception) {
//        return new ApiResponse<>(
//                exception.getBindingResult().getFieldError(),
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.name(),
//                "입력 값을 확인해주세요."
//        );
        return null;
    }

    public static ApiResponse<BindingResult> fail(Exception exception) {
        return new ApiResponse<>(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                exception.getMessage()
        );
    }


}
