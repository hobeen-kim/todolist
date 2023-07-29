package todolist.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import todolist.auth.utils.AuthUtil;
import todolist.global.exception.buinessexception.BusinessException;
import todolist.global.exception.buinessexception.authexception.JwtExpiredAuthException;
import todolist.global.exception.buinessexception.commonexception.UnknownException;
import todolist.global.exception.buinessexception.requestexception.RequestNotAllowedException;

import java.io.IOException;

import static todolist.auth.utils.AuthConstant.*;

@Slf4j
@Component
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        BusinessException businessException = (BusinessException) request.getAttribute(BUSINESS_EXCEPTION);
        if(businessException != null){
            AuthUtil.sendErrorResponse(response, businessException);

            if(businessException instanceof JwtExpiredAuthException){
                response.setHeader("Allow", "POST");
                response.setHeader(LOCATION,
                        request.getScheme() + "://" + request.getServerName() +  REFRESH_URL);
            }
            return;
        }

        Exception exception = (Exception) request.getAttribute(EXCEPTION);
        logExceptionMessage(authException, exception);

        authException.printStackTrace();
        AuthUtil.sendErrorResponse(response, new UnknownException());
    }

    private void logExceptionMessage(AuthenticationException authException, Exception exception) {
        String message = exception != null ? exception.getMessage() : authException.getMessage();
        log.warn("Unauthorized error happened: {}", message);
    }

}
