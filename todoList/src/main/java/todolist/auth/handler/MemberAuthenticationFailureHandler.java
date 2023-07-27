package todolist.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import todolist.auth.utils.AuthUtil;
import todolist.global.exception.buinessexception.commonexception.UnknownException;
import todolist.global.exception.buinessexception.memberexception.MemberBadCredentialsException;

import java.io.IOException;

@Slf4j
public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if(exception instanceof BadCredentialsException){
            AuthUtil.sendErrorResponse(response, new MemberBadCredentialsException());
            return;
        }
        log.error("# Authentication failed with unknown reason : {}", exception.getMessage());
        AuthUtil.sendErrorResponse(response, new UnknownException());
    }
}
