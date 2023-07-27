package todolist.global.exception.buinessexception.authexception;

import org.springframework.http.HttpStatus;

public class JwtExpiredAuthException extends AuthException{

    public static final String MESSAGE = "인증 유효기간이 만료되었습니다.";
    public static final String CODE = "AUTH-401";

    public JwtExpiredAuthException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
