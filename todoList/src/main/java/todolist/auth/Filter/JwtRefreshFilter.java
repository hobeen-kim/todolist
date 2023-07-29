package todolist.auth.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;
import todolist.auth.handler.MemberRefreshFailureHandler;
import todolist.auth.utils.AuthUtil;
import todolist.auth.service.TokenProvider;
import todolist.global.exception.buinessexception.BusinessException;
import todolist.global.exception.buinessexception.authexception.AuthException;
import todolist.global.exception.buinessexception.authexception.JwtNotFoundAuthException;
import todolist.global.exception.buinessexception.requestexception.RequestNotAllowedException;

import java.io.IOException;

import static todolist.auth.utils.AuthConstant.*;

@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final MemberRefreshFailureHandler refreshFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //POST 방식이 아니면 refresh 페이지를 포함해서 다시 예외를 던진다.
        if(!request.getMethod().equals("POST")){
            this.refreshFailureHandler.onAuthenticationFailure(request, response, new RequestNotAllowedException());
        }
        else{
            try {
                String refreshToken = getRefreshToken(request);
                if(refreshToken == null){
                    throw new JwtNotFoundAuthException();
                }
                tokenProvider.validateToken(refreshToken);

                String regeneratedAccessToken = tokenProvider.generateAccessTokenFromRefreshToken(refreshToken, ACCESS_TOKEN_EXPIRE_TIME);

                response.setHeader(AUTHORIZATION, BEARER + regeneratedAccessToken);

            //모든 예외는 이곳에서 처리된다.
            }catch(Exception exception){
                this.refreshFailureHandler.onAuthenticationFailure(request, response, exception);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return !request.getRequestURI().equals(REFRESH_URL);
    }

    private String getRefreshToken(HttpServletRequest request) {

        return request.getHeader(REFRESH);
    }
}
