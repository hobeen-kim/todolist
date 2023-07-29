package todolist.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import todolist.auth.utils.AuthUtil;
import todolist.global.exception.buinessexception.BusinessException;
import todolist.global.exception.buinessexception.authexception.AuthException;
import todolist.global.exception.buinessexception.commonexception.UnknownException;
import todolist.global.exception.buinessexception.memberexception.MemberBadCredentialsException;
import todolist.global.exception.buinessexception.requestexception.RequestException;

import java.io.IOException;

import static todolist.auth.utils.AuthConstant.*;

@Slf4j
public class MemberRefreshFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException, ServletException {

        if(exception instanceof AuthException){

            response.setHeader("Allow", "POST");
            response.setHeader(LOCATION, request.getScheme() + "://" + request.getServerName() +  LOGIN_PATH);
            AuthUtil.sendErrorResponse(response, (AuthException) exception);
            return;
        }

        if(exception instanceof RequestException){
            response.setHeader("Allow", "POST");
            response.setHeader(LOCATION, request.getScheme() + "://" + request.getServerName() +  REFRESH_URL);
            AuthUtil.sendErrorResponse(response, (RequestException) exception);
            return;
        }

        if(exception instanceof BusinessException){
            AuthUtil.sendErrorResponse(response, (BusinessException) exception);
            return;
        }

        log.error("# Authentication failed with unknown reason : {}", exception.getMessage());
        AuthUtil.sendErrorResponse(response, new UnknownException());
    }
}
