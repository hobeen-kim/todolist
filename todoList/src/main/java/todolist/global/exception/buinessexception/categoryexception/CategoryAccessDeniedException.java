package todolist.global.exception.buinessexception.categoryexception;

import org.springframework.http.HttpStatus;

public class CategoryAccessDeniedException extends CategoryException {

    public static final String MESSAGE = "접근 권한이 없는 카테고리입니다.";
    public static final String CODE = "CATEGORY-403";

    public CategoryAccessDeniedException() {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE);
    }
}
