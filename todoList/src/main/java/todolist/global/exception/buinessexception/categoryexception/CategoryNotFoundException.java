package todolist.global.exception.buinessexception.categoryexception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CategoryException {

    public static final String MESSAGE = "존재하지 않거나 삭제된 카테고리입니다.";
    public static final String CODE = "CATEGORY-404";

    public CategoryNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
