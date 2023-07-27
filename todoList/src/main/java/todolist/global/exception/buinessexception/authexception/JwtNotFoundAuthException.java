package todolist.global.exception.buinessexception.authexception;

import org.springframework.http.HttpStatus;

public class JwtNotFoundAuthException extends AuthException{

    public static final String MESSAGE = "토큰 정보가 필요합니다.";
    public static final String CODE = "AUTH-400";

    public JwtNotFoundAuthException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
